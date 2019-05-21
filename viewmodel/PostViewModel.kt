package com.example.test.viewmodel


/**
 * View Model class for handlind post data
 * Uses dependency injection from Dagger2
 */
class PostViewModel @Inject constructor(var repository: PostRepository): ViewModel(){

    // Refresh data
    private val POST_KEY = "POST_KEY"
    private val postRateLimiter = RateLimiter<String>(10, TimeUnit.MINUTES)

    private var _resetAll = MutableLiveData<Pair<String, Boolean>>()
    private val showPosts = MutableLiveData<String>()

    val resetAll: LiveData<Pair<String, Boolean>>
        get() = _resetAll


    // Get Post Live Data for User
    var posts : LiveData<PagedList<Post>> = Transformations.switchMap(showPosts) { userId ->
        repository.getPosts(userId)
    }

    /**
     * Sets user Id to LiveData
     * @param userId String
     */
    fun showPosts(userId: String) {
        // Set user Id so that switchMap is triggered
        showPosts.value = userId

        // Returns true the first time when called and then always
        // returns falls till 10 minutes are elapsed
        if(postRateLimiter.shouldFetch(POST_KEY)) {
            // Using View Model scope from Kotlin coroutines
            viewModelScope.launch {

                // Coroutines delay function, so that first what ever is in the database is thrown at the user,
                // then fresh data is requested after 3 seconds
                delay(3000)
                repository.refreshPost(userId)
            }
        }
    }

    /**
     * Requests for new dataand when received deletes all the data from the database and adds
     * new one
     */
    fun refreshAll(userId: String, userId: String) {
        repository.refreshPost(userId = userId)
    }

}
