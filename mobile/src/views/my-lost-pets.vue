<template>
  <div class="my-lost-pets">
    <h1>我的发布</h1>
    <div v-for="pet in lostPets" :key="pet.id" class="pet-item">
      <h3>{{ pet.petName }}</h3>
      <p>类型: {{ pet.petType }}</p>
      <p>丢失地点: {{ pet.lostLocation }}</p>
      <p>状态: {{ pet.status }}</p>
      <button @click="viewDetails(pet.id)">查看详情</button>
    </div>
    <button @click="goToPublish">发布新信息</button>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      lostPets: []
    };
  },
  mounted() {
    this.fetchLostPets();
  },
  methods: {
    async fetchLostPets() {
      try {
        const userId = 1; // TODO: 获取当前用户ID
        const response = await axios.get(`http://localhost:8080/api/lost-pets/user/${userId}`);
        this.lostPets = response.data;
      } catch (error) {
        alert('获取失败: ' + error.message);
      }
    },
    viewDetails(id) {
      this.$router.push(`/detail/${id}`);
    },
    goToPublish() {
      this.$router.push('/publish');
    }
  }
};
</script>

<style>
.my-lost-pets {
  padding: 20px;
}
.pet-item {
  border: 1px solid #ccc;
  padding: 10px;
  margin-bottom: 10px;
}
button {
  margin-top: 10px;
  padding: 8px;
  background-color: #007bff;
  color: white;
  border: none;
}
</style>