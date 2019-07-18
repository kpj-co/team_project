
# 💀 README Project Skeleton 💀

## 1. User Stories (Required and Optional)

**Required Must-have Stories**

 * Username and password
 * Search School 
 * Choose courses form a school 
 * Activity with all courses the user has selected 
 * Feed of a course with all the posts 
 * Ability to compose an image post
 * Ability to compse a text post 
 * Ability to upvote and downvote posts
 * Ability to click for post details 
 * Ability to comment on posts 
 * Ability to upvote and downvote comments
 * Ability to choose suggested post hashtags (mandatory hashtags i.e. teacher, course year, section)
 * Ability to access chat messages 
 * Ability to send chat messages 
 * Ability to "send" a post to the chat 
 * Ability to turn own chat message into a post 
 * Ability to filter posts by hashtags 

**Optional Nice-to-have Stories**
 * Ability to make their own post hashtags 
 * Autocomplete hashtags
 * Search for a course in the school 
 * Ability to choose suggested post hashtags
 * Ability to send comments to chat 
 * Ability to see user profile to change info 
 * Ability to choose more than one school to view courses of 
 * Ability to report posts 
 * Divide Signup and login in two activities
 

## 2. Screen Archetypes

 * [Login]
   * [Username and Password]
   * [Login Button]
 * [Page of selected courses]
   * [cards of all courses selected with small graphic]
   * [Button to add more courses]
   * [Button to delete a course]
   * [Show course by most recently clicked]
 * [Course Feed]
   * [Recycler view] 
   * [Nav bar]
   * [Search bar to filter posts by hashtags]
   * [All posts associated with course]
   * [Respective user who posted the post] 
   * [Post upvote number] 
   * [Post downvote number] 
   * [Compose post button] 
   * [Send post to chat button]
 * [Compose Post]
   * [Title] 
   * [Discription/ Text body] 
   * [Image]
   * [Choose Hashtags] 
   * [Send button]
 * [Post Detail (When a post is clicked)]
   * [Nav bar]
   * [Username of person who posted respective post]
   * [Title] 
   * [Discription/Text body] 
   * [Image]
   * [Associated post hashtags]
   * [Button to send post to chat]
   * [Comments]
       * [Username] 
       * [User text comment]
       * [Upvote on comment]
       * [Downvote on comment]
       * [Send comment to chat] (Would be nice)
 * [Message/Chat]
   * [Recycler view]
   * [Other users and respective messages] 
   * [Text messages]
   * [Image messages]
   * [Edit text for user to write message]
   * [Send message]
   * [Preview a post link and add text]
   * [Post link messages]
   * [Functionality to make own chat message into post] 
       * When the user press the button, the will be directed to compose activity. 
       * The fields will be already filled. 
       * The user will have the posibility to modify these fields before posting
 * [Sign Up]
   * [Edit text to create username]
   * [Edit text to create password]
   * [Finish button]
 * [Find University] 
   * [Search bar to find university]
   * [Next button]
 * [Choose Course] 
   * [Search bar to find course by course code]
   * [Recycler view]
   * [Break courses by subject]
   * [Course options with the course code]
   * [Next button]
   
## 3. Navigation

**Courses Navigation** 
 * Includes all the courses the user has selected 
 * Is shown by touching a hamburger icon
 * Covers half the screen (?)

*Bottom Navigation** (?) maybe
 * Chat tab 
 * Feed tab 

**Flow Navigation** (Screen to Screen)

 * [Login]
   * [Sign Up]
   * [Unviersity Selection]
 * [University Selection]
   * [All courses]
 * [All courses]
   * [Page of user selected courses]
 * [Page of user selected courses]
   * [Course Feed]
 * [Course Feed]
   * [Page of user selected courses]
       * Menu version, not an intent
   * [Message/Chat]
   * [Compost Post]
   * [Post Details]
 * [Post Details]
   * [Message/Chat]
   * [Course Feed]  
       * We can go back using the stack of activities 
 * [Message/Chat]
   * [Course Feed]   
       * To where you were scrolling at before you entered chat fragement 
   * [Post Details]
       * Not to be confused with course feed, this is for when a person sends a link and the user clicks on the link
   * [Compose Post] 
       * When you want to post your message 
 * [Compost Post] 
   * [Course Feed] 
       * Automatic transition after sent is pressed
     

