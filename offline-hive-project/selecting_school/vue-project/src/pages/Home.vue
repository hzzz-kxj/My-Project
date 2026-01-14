<!-- src/components/Home.vue -->
<template>
  <div class="homepage">
    <!-- 顶部横幅 -->
    <header class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">智能择校平台</h1>
        <p class="hero-subtitle">为您提供最专业的学校选择建议和数据分析</p>
        <div class="hero-buttons">
          <RouterLink to="/Login" class="btn btn-primary">立即登录</RouterLink>
          <RouterLink to="/Register" class="btn btn-secondary">免费注册</RouterLink>
        </div>
      </div>
      <div class="hero-image">
        <!-- 这里可以放图片 -->
      </div>
    </header>

    <!-- 功能特点 -->
    <section class="features-section">
      <h2 class="section-title">平台特色</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">
            <i class="fas fa-chart-bar"></i>
          </div>
          <h3>数据分析</h3>
          <p>基于大数据的学校评估和排名分析</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <i class="fas fa-graduation-cap"></i>
          </div>
          <h3>专业推荐</h3>
          <p>根据学生特点智能推荐适合的学校</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <i class="fas fa-comments"></i>
          </div>
          <h3>专家咨询</h3>
          <p>提供专业的择校指导和咨询服务</p>
        </div>
      </div>
    </section>

    <!-- 热门学校 -->

  </div>
</template>

<script setup lang="ts">
// 首页逻辑
</script>

<style scoped>
.homepage {
  width: 100%;
}

/* 英雄区域 */
.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 80px 20px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: bold;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
}

.hero-subtitle {
  font-size: 1.5rem;
  margin-bottom: 40px;
  opacity: 0.9;
  max-width: 600px;
}

.hero-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.btn {
  padding: 12px 30px;
  border-radius: 50px;
  font-size: 1.1rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
}

.btn-primary {
  background: white;
  color: #667eea;
}

.btn-secondary {
  background: transparent;
  color: white;
  border: 2px solid white;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.2);
}

/* 功能特点 */
.features-section {
  padding: 80px 20px;
  background: #f8f9fa;
}

.section-title {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 60px;
  color: #333;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.feature-card {
  background: white;
  padding: 40px 30px;
  border-radius: 15px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-10px);
}

.feature-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 30px;
  color: white;
  font-size: 2rem;
}

.feature-card h3 {
  font-size: 1.5rem;
  margin-bottom: 15px;
  color: #333;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
}

/* 学校推荐 */
.schools-section {
  padding: 80px 20px;
}

.schools-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.school-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s ease;
}

.school-card:hover {
  transform: translateY(-5px);
}

.school-image {
  height: 200px;
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.school-info {
  padding: 20px;
}

.school-info h3 {
  font-size: 1.3rem;
  margin-bottom: 10px;
  color: #333;
}

.school-info p {
  color: #666;
}

/* CTA区域 */
.cta-section {
  background: linear-gradient(135deg, #4CAF50, #388E3C);
  color: white;
  text-align: center;
  padding: 80px 20px;
}

.cta-section h2 {
  font-size: 2.5rem;
  margin-bottom: 20px;
}

.cta-section p {
  font-size: 1.2rem;
  margin-bottom: 40px;
  opacity: 0.9;
}

.btn-large {
  background: white;
  color: #4CAF50;
  padding: 15px 50px;
  font-size: 1.2rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }

  .hero-subtitle {
    font-size: 1.2rem;
  }

  .hero-buttons {
    flex-direction: column;
    align-items: center;
  }

  .section-title {
    font-size: 2rem;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }
}
</style>
