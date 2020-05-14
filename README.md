# GoogleBooks

App which allows user to search Google Books, displaying a list of results matching their search query.  App is made in MVVM architecture, using no third party libraries.  1st party libraries from Google are used such as Volley for networking and Dagger for dependency injection.  Image downloading is done through an AsyncTask rather than Glide or Picasso.  It also uses a Room database to cache most recent queries for a set amount of time, and I have included a few unit tests for the cache functionality.

![image](https://user-images.githubusercontent.com/44408528/81975463-ae75d600-95ec-11ea-91c7-b67e28e5e3e9.png)
