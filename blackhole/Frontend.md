# New concepts Learned : 

```java
##  MULTIPLE CONEPTS WERE LEARNED ITz EASIER TO DUMP GPT 
    EXPLANATION HERE FOR LATER REVISION
```


```text
HTML creates elements
        ↓
document finds those elements
        ↓
addEventListener waits for something to happen
        ↓
callback reads inputs / calls backend / changes page
```

React and Angular hide much of this behind components, bindings, hooks, and templates. Here we are touching the browser APIs directly.

## 1. What is `document`?

`document` is the browser’s JavaScript representation of the current HTML page—the DOM, or Document Object Model.

Suppose the HTML contains:

```html
<p id="status"></p>
```

JavaScript finds it with:

```javascript
const status = document.querySelector("#status");
```

`querySelector` accepts a CSS selector:

```javascript
document.querySelector("#status");   // element with id="status"
document.querySelector(".message");  // first element with class="message"
document.querySelector("button");    // first <button>
```

The result is an actual JavaScript object representing that HTML element.

You can then change it:

```javascript
status.textContent = "Login failed";
```

The browser updates the visible page immediately.

`textContent` is especially useful for messages because it treats the value as plain text. If somebody sends:

```html
<script>alert("hello")</script>
```

it is displayed as text instead of being executed.

You can see this in [login.html](/Users/mac/Desktop/WebSockets/SCOK/src/main/resources/static/login.html:38).

---

## 2. Detecting forms and button presses

### Form submission

The login form has:

```html
<form id="login-form">
    <input id="login-email">
    <input id="login-password">
    <button type="submit">Login</button>
</form>
```

JavaScript registers a function that should run whenever this form is submitted:

```javascript
document
    .querySelector("#login-form")
    .addEventListener("submit", async event => {
        // Runs later, when the form is submitted
    });
```

Breaking that down:

```javascript
document.querySelector("#login-form")
```

Find the form.

```javascript
.addEventListener("submit", callback)
```

Tell the browser: “When this form submits, call this function.”

```javascript
async event => {
}
```

This is an arrow function. It is approximately equivalent to:

```javascript
async function (event) {
}
```

The browser passes an event object containing information about what happened.

### Why `submit`, not `click`?

Listening to the form’s `submit` event covers both:

- Clicking the submit button
- Pressing Enter inside an input

That is preferable to listening only for the button’s `click`.

### What does `preventDefault()` do?

Normally, an HTML form performs its traditional browser behavior:

1. Submit to its `action`
2. Reload or navigate away from the page
3. Let the server return another HTML page

That is how this would work:

```html
<form action="/login" method="post">
```

We want to send JSON with JavaScript without reloading. Therefore:

```javascript
event.preventDefault();
```

means:

> Cancel the browser’s normal form submission. My JavaScript will handle it.

The complete listener is in [login.html](/Users/mac/Desktop/WebSockets/SCOK/src/main/resources/static/login.html:52).

### Ordinary button clicks

A button that doesn’t represent a form can listen directly for `click`:

```javascript
document
    .querySelector("#logout")
    .addEventListener("click", SCOK.logout);
```

This means:

> Find the logout button. When it is clicked, call `SCOK.logout`.

For leaving a room:

```javascript
document.querySelector("#leave").addEventListener("click", async () => {
    // Leave-room logic
});
```

That is in [chat.html](/Users/mac/Desktop/WebSockets/SCOK/src/main/resources/static/chat.html:122).

Notice the difference:

```javascript
.addEventListener("click", SCOK.logout);
```

passes the function itself.

This would be wrong:

```javascript
.addEventListener("click", SCOK.logout());
```

The parentheses would execute `logout()` immediately while loading the page instead of waiting for a click.

---

## 3. Reading an input

Given:

```html
<input id="login-email">
```

We find it and read its current value:

```javascript
const email = document.querySelector("#login-email").value;
```

The element is:

```javascript
document.querySelector("#login-email")
```

Its entered text is:

```javascript
.value
```

Therefore the login handler eventually calls:

```javascript
await login(
    document.querySelector("#login-email").value,
    document.querySelector("#login-password").value
);
```

---

## 4. How the HTTP API requests work

The browser provides `fetch()` for HTTP requests.

Your login request is:

```javascript
const response = await fetch("/api/auth/login", {
    method: "POST",
    headers: {
        "Content-Type": "application/json"
    },
    body: JSON.stringify({
        email,
        password
    })
});
```

### The URL

```javascript
"/api/auth/login"
```

Because it starts with `/`, the browser uses the current server:

```text
http://localhost:8080/api/auth/login
```

After deployment, the exact same code becomes:

```text
https://your-domain.com/api/auth/login
```

Nothing needs to be hardcoded.

### The options object

The second `fetch` argument is an ordinary JavaScript object:

```javascript
{
    method: "POST",
    headers: {...},
    body: ...
}
```

Your Spring controller expects JSON, so we declare:

```javascript
headers: {
    "Content-Type": "application/json"
}
```

### `JSON.stringify`

JavaScript starts with an object:

```javascript
{
    email: "user@example.com",
    password: "password123"
}
```

`JSON.stringify` turns it into the JSON text sent over HTTP:

```json
{"email":"user@example.com","password":"password123"}
```

### `await`

`fetch` takes time, so it returns a `Promise`. `await` pauses this particular asynchronous function until the response arrives:

```javascript
const response = await fetch(...);
```

It does not freeze the whole browser.

### Reading the JSON response

```javascript
const body = await response.json();
```

If Spring returns:

```json
{
  "accessToken": "abc",
  "refreshToken": "xyz",
  "userId": 1,
  "username": "langost"
}
```

then JavaScript creates an object, letting us access:

```javascript
body.accessToken
body.userId
body.username
```

### Detecting HTTP errors

`fetch` does not automatically throw an error for `400`, `401`, or `500`. You check:

```javascript
if (!response.ok) {
    throw new Error("Login failed");
}
```

`response.ok` is true for successful `2xx` responses.

The surrounding `try/catch` catches that error:

```javascript
try {
    await login(email, password);
} catch (error) {
    status.textContent = error.message;
}
```

---

## 5. How authentication works

The authentication lifecycle is mostly located in [app.js](/Users/mac/Desktop/WebSockets/SCOK/src/main/resources/static/app.js:1).

### Login

Login sends email/password to:

```text
POST /api/auth/login
```

Spring returns:

```json
{
  "accessToken": "...",
  "refreshToken": "...",
  "userId": 1,
  "username": "langost"
}
```

We save these values in browser storage:

```javascript
localStorage.setItem("accessToken", auth.accessToken);
localStorage.setItem("refreshToken", auth.refreshToken);
localStorage.setItem("userId", auth.userId);
localStorage.setItem("username", auth.username);
```

`localStorage` persists across page navigation and browser restarts.

You retrieve something with:

```javascript
localStorage.getItem("accessToken");
```

And remove it with:

```javascript
localStorage.removeItem("accessToken");
```

### Authenticated REST requests

Instead of repeating authentication logic everywhere, rooms and chat use:

```javascript
SCOK.api("/api/room/mine");
```

The wrapper adds the JWT:

```javascript
headers.set(
    "Authorization",
    `Bearer ${localStorage.getItem("accessToken")}`
);
```

That produces:

```http
Authorization: Bearer eyJhbGciOi...
```

Your `JwtAuthFilter` receives that header, validates the access token and establishes the authenticated user for the HTTP request.

### Expired access tokens

If Spring returns `401`, the wrapper calls:

```text
POST /api/auth/refresh
```

using the saved refresh token:

```javascript
if (
    response.status === 401 &&
    retry &&
    await refreshAccessToken()
) {
    return api(path, options, false);
}
```

If refresh succeeds:

1. Store the new access and refresh tokens.
2. Repeat the original request.
3. Pass `false` so it cannot retry forever.

That logic is in [app.js](/Users/mac/Desktop/WebSockets/SCOK/src/main/resources/static/app.js:50).

### `requireSession()`

Protected-looking pages begin with:

```javascript
SCOK.requireSession();
```

It checks whether an access token exists:

```javascript
if (!localStorage.getItem("accessToken")) {
    location.replace("/login.html");
}
```

This is only frontend navigation behavior, not real security. Real security still comes from Spring protecting `/api/**`.

Someone can manually open `rooms.html`, but they cannot retrieve rooms without a valid JWT.

### WebSocket authentication

REST authentication and WebSocket authentication use the same access token through different transports.

For REST:

```http
Authorization: Bearer token
```

For STOMP:

```javascript
client.connectHeaders = {
    Authorization: `Bearer ${SCOK.accessToken()}`
};
```

That produces an `Authorization` native header on the STOMP `CONNECT` frame.

Your flow is:

```text
Browser opens /ws
    ↓
Browser sends STOMP CONNECT + JWT
    ↓
JwtStompAuthInterceptor validates JWT
    ↓
Interceptor sets Principal
    ↓
@MessageMapping receives that Principal
```

The relevant frontend code is in [chat.html](/Users/mac/Desktop/WebSockets/SCOK/src/main/resources/static/chat.html:76).

---

## 6. How room elements are created

Your room list didn’t exist in the original HTML because the rooms come from the database.

JavaScript receives an array:

```javascript
[
    {id: 1, name: "General"},
    {id: 2, name: "Testing"}
]
```

Then loops over it:

```javascript
for (const room of rooms) {
}
```

For every room, it creates HTML objects:

```javascript
const item = document.createElement("div");
const text = document.createElement("span");
const button = document.createElement("button");
```

At this point, they exist in memory but are not yet visible.

We configure them:

```javascript
item.className = "room";
text.textContent = `${room.name} (#${room.id})`;
button.textContent = "Open";
```

Then attach button behavior:

```javascript
button.addEventListener("click", () => openRoom(room.id));
```

Finally, we place the elements into the page:

```javascript
item.append(text, button);
roomsElement.append(item);
```

This creates approximately:

```html
<div class="room">
    <span>General (#1)</span>
    <button>Open</button>
</div>
```

`append` only places elements inside other elements. It does not control left/right positioning.

This is in [rooms.html](/Users/mac/Desktop/WebSockets/SCOK/src/main/resources/static/rooms.html:49).

---

## 7. Why your messages appear on the right

This has two parts: JavaScript determines ownership, and CSS determines position.

### JavaScript identifies your messages

Each message from Spring contains:

```json
{
  "senderId": 1,
  "senderUsername": "langost",
  "content": "Hello"
}
```

Your logged-in user ID was stored during login.

We compare them:

```javascript
if (String(message.senderId) === SCOK.userId()) {
    bubble.classList.add("mine");
}
```

`localStorage` always returns strings, so:

```javascript
String(message.senderId)
```

converts the numeric backend ID into a string before comparison.

If you sent the message, the generated element becomes:

```html
<div class="message mine">
```

Otherwise it remains:

```html
<div class="message">
```

That happens in [chat.html](/Users/mac/Desktop/WebSockets/SCOK/src/main/resources/static/chat.html:46).

### CSS moves `.mine` to the right

Every message normally has:

```css
.message {
    width: fit-content;
    margin: 8px 0;
    background: #444;
}
```

A message with the additional `mine` class gets:

```css
.message.mine {
    margin-left: auto;
    background: #167d40;
}
```

`margin-left: auto` consumes all available space on the left side, pushing the bubble to the right.

So the responsible code is:

```css
margin-left: auto;
```

It is **not** `space-between`, and it is not `bubble.append()`.

`space-between` is used elsewhere to place two separate items at opposite ends of a flex row—for example, the room name on the left and the Open button on the right.

The bubble CSS is in [styles.css](/Users/mac/Desktop/WebSockets/SCOK/src/main/resources/static/styles.css:121).

---

## 8. How live messages enter the page

First, REST loads old messages:

```javascript
const [room, messages] = await Promise.all([
    SCOK.api(`/api/room/${roomId}`),
    SCOK.api(`/api/room/${roomId}/messages`)
]);

messages.forEach(addMessage);
```

`Promise.all` runs both HTTP requests concurrently and returns both results.

Then WebSocket/STOMP handles new messages:

```javascript
client.subscribe(`/topic/rooms/${roomId}`, frame => {
    addMessage(JSON.parse(frame.body));
});
```

Whenever Spring broadcasts to that topic:

1. The callback receives a STOMP frame.
2. `frame.body` contains JSON text.
3. `JSON.parse` changes it into a JavaScript object.
4. `addMessage` creates and appends the bubble.

Sending works in the opposite direction:

```javascript
client.publish({
    destination: `/app/rooms/${roomId}/messages`,
    headers: {"content-type": "application/json"},
    body: JSON.stringify({content: input.value})
});
```

That sends:

```text
Browser
  → /app/rooms/1/messages
  → @MessageMapping
  → MessageService saves it
  → @SendTo broadcasts it
  → /topic/rooms/1
  → every subscribed browser calls addMessage()
```

Your own browser also receives the broadcast. We don’t immediately draw the message when Send is pressed; we wait for the server to accept, save and broadcast it. That prevents displaying messages the backend rejected.

---

## 9. A few modern syntax shortcuts

### Template literals

Backticks allow values inside strings:

```javascript
`/api/room/${roomId}/messages`
```

If `roomId` is `5`, the result is:

```text
/api/room/5/messages
```

### Object shorthand

This:

```javascript
JSON.stringify({email, password})
```

is shorthand for:

```javascript
JSON.stringify({
    email: email,
    password: password
});
```

### Optional chaining

```javascript
client?.connected
```

means:

```javascript
client != null && client.connected
```

Likewise:

```javascript
body?.message
```

only reads `message` if `body` exists.

### Spread syntax

```javascript
fetch(path, {...options, headers});
```

copies everything from `options`, then replaces or adds `headers`.

If `options` is:

```javascript
{method: "POST", body: "..."}
```

the resulting object is effectively:

```javascript
{
    method: "POST",
    body: "...",
    headers: headers
}
```

### `const` and `let`

Use `const` when the variable itself won’t be reassigned:

```javascript
const roomId = 5;
```

Use `let` when it will be assigned or changed later:

```javascript
let client;
client = new StompJs.Client(...);
```

Objects declared with `const` can still be modified; `const` only prevents reassigning the variable to an entirely different object.

The simplest way to read this frontend is in this order:

1. [login.html](/Users/mac/Desktop/WebSockets/SCOK/src/main/resources/static/login.html:38)
2. [app.js](/Users/mac/Desktop/WebSockets/SCOK/src/main/resources/static/app.js:1)
3. [rooms.html](/Users/mac/Desktop/WebSockets/SCOK/src/main/resources/static/rooms.html:39)
4. [chat.html](/Users/mac/Desktop/WebSockets/SCOK/src/main/resources/static/chat.html:36)
5. [styles.css](/Users/mac/Desktop/WebSockets/SCOK/src/main/resources/static/styles.css:110)