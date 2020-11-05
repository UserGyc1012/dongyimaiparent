app.service("contentService",function ($http){
    //根据分类id查询广告
    this.findByCategoryId=function (categoryId){
      return  $http.get('content/findContentList.do?categoryId='+categoryId);
    }
})