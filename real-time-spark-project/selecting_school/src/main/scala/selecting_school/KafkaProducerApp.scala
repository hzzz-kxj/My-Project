import java.util.Properties
import org.apache.kafka.clients.producer._
import scala.util.Random

object KafkaProducerApp {
  def main(args: Array[String]): Unit = {
    val props = new Properties()
    props.put("bootstrap.servers", "hadoop102:9092,hadoop103:9092,hadoop104:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("acks", "1")
    props.put("linger.ms", "50")
    props.put("batch.size", "16384")
    props.put("compression.type", "snappy")

    val producer = new KafkaProducer[String, String](props)
    val topic = "university_indicator_calc"

    // 读取本地CSV文件
    val source = scala.io.Source.fromFile("G:\\Study\\课设\\大三上课设\\数据库相关\\mock_kafka_data.csv")
    val lines = source.getLines().toList
    source.close()

    // 获取数据行（跳过标题行）
    val dataLines = lines.tail

    // 创建随机数生成器
    val random = new Random()

    // 生成随机索引列表（按文件长度随机读取）
    val totalLines = dataLines.length
    val randomIndices = (1 to totalLines).map(_ => random.nextInt(totalLines)).distinct.take(totalLines)

    println(s"文件总数据行数: $totalLines")
    println(s"随机读取顺序已生成，将按随机顺序发送 ${randomIndices.length} 条数据")

    // 按随机顺序发送数据
    randomIndices.zipWithIndex.foreach { case (randomIndex, sendIndex) =>
      val line = dataLines(randomIndex)
      val record = new ProducerRecord[String, String](topic, sendIndex.toString, line)
      producer.send(record)
      println(s"Sent [$sendIndex/$totalLines] (原始行: $randomIndex): $line")
      Thread.sleep(100) // 模拟实时数据流，每秒发送1条
    }

    producer.close()
    println("所有数据发送完成")
  }
}