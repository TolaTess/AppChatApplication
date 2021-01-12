# AppChat

AppChat is a spin the wheel game that gives users challenges to help elevate boredom. This app was built using Java Programming language, Firebase authentication, realtime database, file storage, Picasso image library, Butterknife UI binding library and Volley for API calls. The MVP architecture model was used to keep the code simple, easy to understand and maintain.


* Login and Signup users with Firebase
* API call to generate challenges
* Store accepted challenges in Todo list
* Share challenges with friends
* Request and accept friends
* Account Setting / Profile feature
* Messenger feature

Spin the wheel | Share challenges | Chat with friends
--- | --- | ---
![alt text](https://github.com/TolaTess/AppChatApplication/blob/master/assets/wheel.gif "") |![alt text](https://github.com/TolaTess/AppChatApplication/blob/master/assets/share.gif "") | ![alt text](https://github.com/TolaTess/AppChatApplication/blob/master/assets/friends.gif "")

## Architecture
The architecture used for this application is the MVP(Model, View and Presenter). Using MVP ensures that the code is simple, easy to understand, and able to be changed quickly, making the application efficient.

#### Model Layer:

The Model layer is responsible for handling data within the application. It houses all the principal reusable codes to fetch data from the database (Firebase realtime database) and serves it to the Presenter when needed.

#### View Layer:

The View layer primary responsibility is to render the UI components as directed by the user. This layer includes Activity and Fragment and their respective Presenters. 

#### Presenter Layer:
The Presenter classes house the java functionality for the application. It receives the user interactions from its View and passes back the business feature requested by the View. It also communicates to the Model layer to get data relevant to the exchange. 

## Feature implementation:

#### Login and Signup for users with Firebase
StartActivity class instantiates FirebasePresenter and IntentPresenter. StartActivity will redirect the User to HomeActivity class if logged in but if not the StartActivity will render the UI component for user to log in or register.

LoginActivity renders UI component for the user to enter email and password for authentication with Firebase. Successful authentication will direct the user to the IdeaActivity class, and failure will present a Toast message to the user to “try again”.

#### API call to generate challenges
IdeaActivity uses Volley GET method to access boredapi.com to get an unexpected challenge to display in IdeaActivity UI. Challenge call is stored in Firebase if the challenge is accepted.

#### Store accepted challenges in Todo list
TodoActivity class instantiates TodoAapter to display all accepted challenges in the user’s Todo table in the database in a RecyclerView. ItemTouchHelper is added to the Recycler holder to remove the challenge from the database if swiped left. ItemTouchHelper is added to mark the challenge as done if swiped right. 


#### Share challenges with friends
 An onClick was added to the RecyclerView holder in TodoAdapter, to display a dialog fragment controlled by DialogFragmentHelper class, which will show user’s friend list and prompt the user to share the challenge with a friend. 

#### Request and accept friends
ProfileActivity class instantiates FirebasePresenter and users variables needed for UI components. The user can interact with the request friend button, which will create a request for the receiving user in the database.
RequestsFragment class instantiates RequestsPresenter and renders these requests using Firebase Adapter and RequestViewHolder class to help bind request data to the RecyclerView. The RequestFragment is only viewable to the receiving users.

#### Messenger feature
ChatFragment class instantiates ChatPresenter and renders each consultation chats using Firebase Adapter and ChatViewHolder class to help bind chat data to the RecyclerView.

ChatAcitivity class will instantiate ChatActivityPresenter class when a chat is opened and renders users messages using a custom Adapter (MessageAdapter) to generate different ViewHolder depending on the message type. 

### Libraries and Technologies:
ButterKnife to bind UI elements
Volley to make API calls
Gson to parse JSON to java objects
Picasso image loader
EdMondo image cropper
CircleImageView for circler image view
BottomNavigationViewEX for bottom navigation

#### Firebase
I used Firebase for this project because it is an application development platform that provides tools to build, improve and grow an efficient application. I used their email and password authentication, realtime database and file storage for images. Firebase also offers push messaging which I will implement as I develop this application further. 

### Improvement
I will continue to improve the codes by implementing further decoupling. I will be introducing another layer between the View and the Presenter to further separate dependencies.

I will be adding push messaging features in future iterations.



