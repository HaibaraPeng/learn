<template>
  <v-app id="app">

  </v-app>
</template>

<script>
export default {
  created() {
    // 获取博客信息
    this.getBlogInfo();
    // 上传访客信息
    this.axios.post("/api/report");
  },
  methods: {
    getBlogInfo() {
      this.axios.get("/api/").then(({data}) => {
        this.$store.commit("checkBlogInfo", data.data);
      });
    }
  }
}
</script>

<style scoped>

</style>