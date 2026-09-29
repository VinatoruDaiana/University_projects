# Social Media Platform - Team BrainFreeze
A social media platform developed for the DISI lab @ UTCN

Tasks related to this project are tracked on the [taiga board](https://tree.taiga.io/project/deaconescudaniel-social-media-platform-disi2026/timeline).

The project uses the following file structure for the backend and frontend

```text
├── README.md
├── docker-compose.yml
├── backend
│ ├── Dockerfile
│ ├── pom.xml
│ └── src
│   ├── main.java.com.disi
│   │   │  └── backend
│   │   │    ├── BackendApplication.java
│   │   │    ├── config
│   │   │    │   └── SecurityConfig.java
│   │   │    ├── controller
│   │   │    │   ├── AuthController.java
│   │   │    │   ├── FriendshipController.java
│   │   │    │   └── UserController.java
│   │   │    ├── dto
│   │   │    │   ├── FriendsListDTO.java
│   │   │    │   ├── RegisterRequest.java
│   │   │    │   └── UserDTO.java
│   │   │    ├── entity
│   │   │    │   ├── Friendship.java
│   │   │    │   ├── FriendshipStatus.java
│   │   │    │   ├── User.java
│   │   │    │   └── UserProfile.java
│   │   │    ├── repository
│   │   │    │   ├── FriendshipRepository.java
│   │   │    │   ├── UserProfileRepository.java
│   │   │    │   └── UserRepository.java
│   │   │    └── service
│   │   │        ├── AuthService.java
│   │   │        ├── FriendshipService.java
│   │   │        └── UserService.java
│   │   └── resources
│   │       ├── application.properties
│   │       ├── data.sql
│   │       └── postman
│   │           └── Postman_Collection.json
│   └── test.java.com.disi.backend
│       └──DemoApplicationTests.java             
└── frontend/
    ├── Dockerfile
    ├── README.md
    ├── eslint.config.js
    ├── index.html
    ├── nginx.conf
    ├── package-lock.json
    ├── package.json
    ├── public
    │   ├── favicon.svg
    │   └── icons.svg
    ├── src
    │   ├── App.css
    │   ├── App.tsx
    │   ├── api
    │   │   ├── auth.ts
    │   │   ├── axios.ts
    │   │   └── friends.ts
    │   ├── assets
    │   │   ├── hero.png
    │   │   ├── react.svg
    │   │   └── vite.svg
    │   ├── components
    │   │   ├── Button.css
    │   │   ├── Button.tsx
    │   │   ├── Card.css
    │   │   ├── Card.tsx
    │   │   ├── Input.css
    │   │   └── Input.tsx
    │   ├── index.css
    │   ├── main.tsx
    │   ├── models
    │   │   ├── auth.ts
    │   │   └── friends.ts
    │   ├── pages
    │   │   ├── FriendsList.css
    │   │   ├── FriendsList.tsx
    │   │   ├── Register.css
    │   │   └── Register.tsx
    │   └── vite-env.d.ts
    ├── tsconfig.json
    ├── tsconfig.node.json
    └── vite.config.ts
```

Run the following command from the root to build the project
```bash
docker compose up --build
```
The backend uses the DevTools dependency to automatically reload the code whenever it is modified, so no rebuilding is necessary after making modifications. This makes debugging using print statements easier

To test the api, use the included postman collection or run the following command to make a simple GET request to the backend api
```bash
curl -X GET http://localhost:8081/api/v1/users/
```
