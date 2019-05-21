package com.example.test.repository

class PostRepository @Inject constructor(
        private var db: AppDatabase,
        private var postDao: PostDao,
        private var appExecutors: AppExecutors,
        private var apiService: ApiService,
        private var user: User
) {
    private var firebaseManager = FirebaseManager()

    /**
     *  Creates data source factory and builds LiveData of PagedList using LivePagedListBuilder
     */
    fun getPosts(userId: String): LiveData<PagedList<Post>>? {
        val factory = postDao.allPostsForUserWith(userId)
        factory.create()
        val boundaryCallback = PostBoundaryCallback(userId, postDao, apiService, appExecutors)
        return LivePagedListBuilder(factory, 10)
                .setBoundaryCallback(boundaryCallback)
                .setFetchExecutor(appExecutors.diskIO())
                .build()
    }


    /**
     * When response is recieved, old data is deleted from the database and new data is added
     */
    fun refreshPost(userId: String, page: Int = 1){
        appExecutors.networkIO().execute {
            val response = apiService.getPostsForUser(page, userId)
            response.enqueue(object : Callback<List<Post>> {
                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    // Handle network error
                    Timber.e(t)
                }

                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (response.isSuccessful) {
                        val list = response.body()
                        appExecutors.diskIO().execute {
                            if (list != null) {
                                db.runInTransaction {
                                    postDao.deleteAll()
                                    postDao.insertAll(list)
                                }
                            }
                        }
                    }
                }
            })
        }

    }

}
