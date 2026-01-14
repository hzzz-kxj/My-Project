import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010._
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import java.sql.{Connection, DriverManager, PreparedStatement}
import org.apache.spark.rdd.RDD
import org.apache.kafka.clients.consumer.ConsumerRecord

object AllMetricsProcessor {

  // MySQL配置
  val mysqlUrl = "jdbc:mysql://localhost:3306/selecting_school"
  val mysqlUser = "root"
  val mysqlPassword = "051210"

  // 写入MySQL的通用方法
  def writeToMySQL(tableName: String, data: List[Array[Any]]): Unit = {
    var connection: Connection = null
    var statement: PreparedStatement = null

    try {
      Class.forName("com.mysql.cj.jdbc.Driver")
      connection = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPassword)

      // 根据表名确定列名
      val (columns, placeholders) = tableName match {
        case "dws_school_rank_top10" => (List("school_name", "school_rank"), "?,?")
        case "dws_province_rate" => (List("yes_or_no", "rate"), "?,?")
        case "dws_student_education_level" => (List("student_education", "count"), "?,?")
        case "dws_major_income_top10" => (List("major_name", "avg_income"), "?,?")
        case "dws_school_student_income_top10" => (List("school_name", "avg_income"), "?,?")
        case "dws_major_number" => (List("major_name", "count"), "?,?")
        case "dws_province_number" => (List("school_address", "count"), "?,?")
        case "dws_school_address_number" => (List("school_address", "count"), "?,?")
        case "dws_major_or_school_rank_preference" => (List("major_or_rank", "preference_count"), "?,?")
        case "dws_family_level_major_preference" => (List("major_name", "student_family_level", "count"), "?,?,?")
        case _ => throw new IllegalArgumentException(s"Unknown table: $tableName")
      }

      val sql = s"""
        INSERT INTO $tableName
        (${columns.mkString(",")})
        VALUES ($placeholders)
      """

      statement = connection.prepareStatement(sql)

      data.foreach { row =>
        for (i <- 0 until row.length) {
          row(i) match {
            case v: String => statement.setString(i + 1, v)
            case v: Int => statement.setInt(i + 1, v)
            case v: Long => statement.setLong(i + 1, v)
            case v: Double => statement.setDouble(i + 1, v)
            case null => statement.setNull(i + 1, java.sql.Types.NULL)
          }
        }
        statement.addBatch()
      }

      statement.executeBatch()
      println(s"Successfully wrote ${data.size} records to $tableName")

    } catch {
      case e: Exception =>
        println(s"Error writing to MySQL table $tableName: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      if (statement != null) statement.close()
      if (connection != null) connection.close()
    }
  }

  def main(args: Array[String]): Unit = {
    // 1. 创建Spark配置
    val sparkConf = new SparkConf()
      .setAppName("AllMetricsProcessor")
      .setMaster("local[*]")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.streaming.kafka.consumer.cache.enabled", "false")
      .set("spark.streaming.stopGracefullyOnShutdown", "true")
      .set("spark.streaming.backpressure.enabled", "true")
      .set("spark.streaming.kafka.maxRatePerPartition", "100")

    // 2. 创建StreamingContext，批次间隔2秒
    val ssc = new StreamingContext(sparkConf, Seconds(2))

    // 设置日志级别
    ssc.sparkContext.setLogLevel("WARN")

    // 3. 创建SparkSession用于DataFrame操作
    val spark = SparkSession.builder()
      .config(sparkConf)
      .getOrCreate()
    import spark.implicits._

    // 4. Kafka配置
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "hadoop102:9092,hadoop103:9092,hadoop104:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "school_metrics_group",
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (true: java.lang.Boolean),
      "auto.commit.interval.ms" -> "1000",
      "session.timeout.ms" -> "30000",
      "request.timeout.ms" -> "40000",
      "heartbeat.interval.ms" -> "10000",
      "max.poll.records" -> "500",
      "fetch.min.bytes" -> "1024",
      "fetch.max.wait.ms" -> "100"
    )

    val topics = Array("university_indicator_calc")

    // 5. 创建Kafka Direct Stream
    println("Creating Kafka stream...")
    println(s"Connecting to Kafka brokers: hadoop102:9092,hadoop103:9092,hadoop104:9092")
    println(s"Topic: ${topics.mkString(", ")}")

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
    )

    // 6. 数据处理逻辑
    stream.foreachRDD { (rdd: RDD[ConsumerRecord[String, String]]) =>
      val startTime = System.currentTimeMillis()

      if (!rdd.isEmpty()) {
        val recordCount = rdd.count()
        println(s"[${new java.util.Date()}] Received $recordCount records from Kafka")

        // 解析CSV数据并创建DataFrame
        val parsedRDD = rdd.map { record: ConsumerRecord[String, String] =>
          try {
            val cols = record.value().split(",")
            if (cols.length >= 11) {
              (
                cols(0).trim, // student_id
                cols(1).trim, // major_id
                cols(2).trim, // student_school
                cols(3).trim, // student_education
                cols(4).trim, // student_family_level
                cols(5).trim.toDouble, // student_income
                cols(6).trim, // student_address
                cols(7).trim, // major_name
                cols(8).trim.toInt, // major_rank
                cols(9).trim.toInt, // school_rank
                cols(10).trim // school_address
              )
            } else {
              null
            }
          } catch {
            case e: Exception =>
              println(s"Error parsing line: ${e.getMessage}")
              null
          }
        }.filter(_ != null)

        val validCount = parsedRDD.count()
        println(s"Valid records: $validCount")

        if (validCount > 0) {
          // 转换为DataFrame
          val df = parsedRDD.toDF(
            "student_id", "major_id", "student_school", "student_education",
            "student_family_level", "student_income", "student_address",
            "major_name", "major_rank", "school_rank", "school_address"
          )

          // 缓存DataFrame以提高性能
          df.cache()

          try {
            // ==================== 指标1: 学校排名TOP10 ====================
            val schoolRankTop10 = df
              .select("student_school", "school_rank")
              .groupBy("student_school")
              .agg(avg("school_rank").cast("int").as("avg_school_rank"))
              .withColumnRenamed("student_school", "school_name")
              .orderBy(asc("avg_school_rank"))
              .limit(10)
              .collect()
              .map(row => Array[Any](row.getString(0), row.getInt(1)))
              .toList

            if (schoolRankTop10.nonEmpty) {
              writeToMySQL("dws_school_rank_top10", schoolRankTop10)
              println("=== School Rank Top10 ===")
              schoolRankTop10.foreach(arr =>
                println(f"School: ${arr(0).asInstanceOf[String]}%-30s Rank: ${arr(1).asInstanceOf[Int]}%5d"))
            }

            // ==================== 指标2: 同省份比例 ====================
            val totalCount = df.count()
            if (totalCount > 0) {
              val provinceRate = df
                .withColumn("yes_or_no",
                  when(col("student_address") === col("school_address"), "yes")
                    .otherwise("no"))
                .groupBy("yes_or_no")
                .agg(count("*").as("count"))
                .withColumn("rate", col("count").cast("double") / totalCount)
                .select("yes_or_no", "rate")
                .collect()
                .map(row => Array[Any](row.getString(0), row.getDouble(1)))
                .toList

              if (provinceRate.nonEmpty) {
                writeToMySQL("dws_province_rate", provinceRate)
                println("=== Province Rate ===")
                provinceRate.foreach(arr =>
                  println(f"Same Province: ${arr(0).asInstanceOf[String]}%-5s Rate: ${arr(1).asInstanceOf[Double]}%.4f"))
              }
            }

            // ==================== 指标3: 学生学历统计 ====================
            val studentEducation = df
              .groupBy("student_education")
              .agg(count("*").as("count"))
              .collect()
              .map(row => Array[Any](row.getString(0), row.getLong(1)))
              .toList

            if (studentEducation.nonEmpty) {
              writeToMySQL("dws_student_education_level", studentEducation)
              println("=== Student Education Level ===")
              studentEducation.foreach(arr =>
                println(f"Education: ${arr(0).asInstanceOf[String]}%-5s Count: ${arr(1).asInstanceOf[Long]}%5d"))
            }

            // ==================== 指标4: 专业收入TOP10 ====================
            val majorIncomeTop10 = df
              .groupBy("major_name")
              .agg(avg("student_income").as("avg_income"))
              .orderBy(desc("avg_income"))
              .limit(10)
              .collect()
              .map(row => Array[Any](row.getString(0), row.getDouble(1)))
              .toList

            if (majorIncomeTop10.nonEmpty) {
              writeToMySQL("dws_major_income_top10", majorIncomeTop10)
              println("=== Major Income Top10 ===")
              majorIncomeTop10.foreach(arr =>
                println(f"Major: ${arr(0).asInstanceOf[String]}%-30s Income: ${arr(1).asInstanceOf[Double]}%.2f"))
            }

            // ==================== 指标5: 学校学生收入TOP10 ====================
            val schoolStudentIncomeTop10 = df
              .groupBy("student_school")
              .agg(avg("student_income").as("avg_income"))
              .withColumnRenamed("student_school", "school_name")
              .orderBy(desc("avg_income"))
              .limit(10)
              .collect()
              .map(row => Array[Any](row.getString(0), row.getDouble(1)))
              .toList

            if (schoolStudentIncomeTop10.nonEmpty) {
              writeToMySQL("dws_school_student_income_top10", schoolStudentIncomeTop10)
              println("=== School Student Income Top10 ===")
              schoolStudentIncomeTop10.foreach(arr =>
                println(f"School: ${arr(0).asInstanceOf[String]}%-30s Income: ${arr(1).asInstanceOf[Double]}%.2f"))
            }

            // ==================== 指标6: 专业选择人数TOP10 ====================
            val majorNumber = df
              .groupBy("major_name")
              .agg(count("*").as("count"))
              .orderBy(desc("count"))
              .limit(10)
              .collect()
              .map(row => Array[Any](row.getString(0), row.getLong(1)))
              .toList

            if (majorNumber.nonEmpty) {
              writeToMySQL("dws_major_number", majorNumber)
              println("=== Major Number Top10 ===")
              majorNumber.foreach(arr =>
                println(f"Major: ${arr(0).asInstanceOf[String]}%-30s Count: ${arr(1).asInstanceOf[Long]}%5d"))
            }

            // ==================== 指标7: 省份选择人数TOP10 ====================
            val provinceNumber = df
              .groupBy("school_address")
              .agg(count("*").as("count"))
              .orderBy(desc("count"))
              .limit(10)
              .collect()
              .map(row => Array[Any](row.getString(0), row.getLong(1)))
              .toList

            if (provinceNumber.nonEmpty) {
              writeToMySQL("dws_province_number", provinceNumber)
              println("=== Province Number Top10 ===")
              provinceNumber.foreach(arr =>
                println(f"Province: ${arr(0).asInstanceOf[String]}%-10s Count: ${arr(1).asInstanceOf[Long]}%5d"))
            }

            // ==================== 指标8: 省份学校数量TOP10 ====================
            val schoolAddressNumber = df
              .select("school_address", "student_school")
              .distinct()
              .groupBy("school_address")
              .agg(count("*").as("count"))
              .orderBy(desc("count"))
              .limit(10)
              .collect()
              .map(row => Array[Any](row.getString(0), row.getLong(1)))
              .toList

            if (schoolAddressNumber.nonEmpty) {
              writeToMySQL("dws_school_address_number", schoolAddressNumber)
              println("=== School Address Number Top10 ===")
              schoolAddressNumber.foreach(arr =>
                println(f"Province: ${arr(0).asInstanceOf[String]}%-10s Count: ${arr(1).asInstanceOf[Long]}%5d"))
            }

            // ==================== 指标9: 专业/学校排名倾向 ====================
            val majorOrSchoolRankPreference = df
              .withColumn("major_or_rank",
                when(col("major_rank") < col("school_rank"), "major_rank")
                  .otherwise("school_rank"))
              .groupBy("major_or_rank")
              .agg(count("*").as("preference_count"))
              .collect()
              .map(row => Array[Any](row.getString(0), row.getLong(1)))
              .toList

            if (majorOrSchoolRankPreference.nonEmpty) {
              writeToMySQL("dws_major_or_school_rank_preference", majorOrSchoolRankPreference)
              println("=== Major or School Rank Preference ===")
              majorOrSchoolRankPreference.foreach(arr =>
                println(f"Preference: ${arr(0).asInstanceOf[String]}%-12s Count: ${arr(1).asInstanceOf[Long]}%5d"))
            }

            // ==================== 指标10: 家庭水平-专业偏好 ====================
            val familyLevelMajorPreference = df
              .groupBy("major_name", "student_family_level")
              .agg(count("*").as("count"))
              .orderBy(asc("student_family_level"), desc("count"))
              .limit(20)
              .collect()
              .map(row => Array[Any](row.getString(0), row.getString(1), row.getLong(2)))
              .toList

            if (familyLevelMajorPreference.nonEmpty) {
              writeToMySQL("dws_family_level_major_preference", familyLevelMajorPreference)
              println("=== Family Level Major Preference Top20 ===")
              familyLevelMajorPreference.foreach(arr =>
                println(f"Major: ${arr(0).asInstanceOf[String]}%-25s Family: ${arr(1).asInstanceOf[String]}%-8s Count: ${arr(2).asInstanceOf[Long]}%5d"))
            }

            println("=" * 50)

          } finally {
            // 释放缓存
            df.unpersist()
          }
        }

        val endTime = System.currentTimeMillis()
        println(s"Total processing time: ${endTime - startTime}ms")
      }
    }

    // 7. 启动Streaming
    println("Starting Spark Streaming for all metrics...")
    ssc.start()
    ssc.awaitTermination()
  }
}