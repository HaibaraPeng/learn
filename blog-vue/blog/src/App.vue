<template>
  <v-app id="app">

  </v-app>
</template>

<script>
export default {
  created() {
    // 获取博客信息
    this.getBlogInfo();
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