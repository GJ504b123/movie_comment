<template>
  <div class="flex flex-col p-2 rounded-xl shadow-lg" @click="gotoDetail">
    <div class="h-72 w-full overflow-hidden">
      <img
        :src="movieInfo.coverUrl"
        alt="电影海报"
        class="w-full h-full object-cover"
      />
    </div>
    <div class="bg-gray-50 p-2">
      <p>电影名称：{{ movieInfo.title }}</p>
      <p>评分：{{ movieInfo.averageScore }}</p>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
// 1. 引入严格对齐合同的首页假数据

const props = defineProps({
    movieInfo: { type: Object, required: true },
  });
const router = useRouter();

const gotoDetail = () => {

  router.push({
    name: "detail",
    params: { id: props.movieInfo.id },
  });
};
</script>
<style scoped></style>
