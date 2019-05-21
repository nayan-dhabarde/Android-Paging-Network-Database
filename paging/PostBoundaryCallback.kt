package com.example.test.paging

class PostUserBoundaryCallback (
        private var userId: String,
        private var postDao: PostDao,
        private var apiService: ApiService,
        private var appExecutors: AppExecutors
): PagedList.BoundaryCallback<Post>() {

    // First page
    private var lastRequestedPage = 1

    // Called when there are no items in the database
    override fun onZeroItemsLoaded() {
        lastRequestedPage = 1
        requestAndSaveData(userId, lastRequestedPage)
    }

    // Called when items from one page are loaded
    override fun onItemAtEndLoaded(itemAtEnd: Post) {
        lastRequestedPage += 1

        requestAndSaveData(userId, lastRequestedPage)
    }

    // Requests for new data and add them to the database
    // As soon as new data is added to the database the value of Live Data created by
    // LivePagedListBuilder is updated and can be observed
    private fun requestAndSaveData(userId: String, page: Int){
        appExecutors.networkIO().execute {
            val response = apiService.getPostsForUser(page, userId)
            response.enqueue(object: Callback<List<Post>>{
                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    Timber.e("Could not fetch data, error: ${t.localizedMessage}")
                }

                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if(response.isSuccessful) {
                        val list = response.body()
                        appExecutors.diskIO().execute {
                            if (list != null) {
                                postDao.insertAll(list)
                            }
                        }
                    }
                }
            })
        }

    }


}