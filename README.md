
# MVVM-Example
      The application contain fetch data from api and stored in local database if the application is offline fetch from local data and show in recyclerview with filter option.
   I use kotlin language,retrofit,mvvm pattern and room database to build this application.

Prerequisites
 1. Add swiperefresh library for push to refresh our api
    implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'

 2. Add retrofit library for receiving data from api and parse.
    implementation 'com.squareup.retrofit2:retrofit:2.6.0'
    implementation 'com.google.code.gson:gson:2.8.5'
    implementation 'com.squareup.retrofit2:converter-gson:2.5.0'
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

 3. Add navigation component for simply navigate and pass data between our fragment
    implementation "androidx.navigation:navigation-fragment-ktx:2.3.0"
    implementation "androidx.navigation:navigation-ui-ktx:2.3.0"

 4. Add glide library to decode image url to load in imageview
    implementation "com.github.bumptech.glide:glide:4.11.0"

 5. Add room dependency to store and retrive data in our local database
    implementation "androidx.room:room-runtime:2.3.0-beta03"
    kapt "androidx.room:room-compiler:2.3.0-beta03"
    implementation "androidx.room:room-ktx:2.3.0-beta03"
