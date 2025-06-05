# TripBook UI Components: PullToRefreshContainer & ReactionBar

This document explains two key Jetpack Compose UI components in the TripBook Android app: `PullToRefreshContainer` and `ReactionBar`.

---

## PullToRefreshContainer

**Location:** `app/src/main/java/com/android/tripbook/posts/PullToRefreshContainer.kt`

### Purpose
`PullToRefreshContainer` is a composable that provides a pull-to-refresh gesture for its content. It is typically used to refresh lists or feeds, allowing users to swipe down to trigger a refresh action.

### Usage
- Wrap your scrollable content (e.g., a list) inside `PullToRefreshContainer`.
- Pass a `refreshing` boolean to indicate if a refresh is in progress.
- Provide an `onRefresh` lambda to handle the refresh action.

**Example:**
```kotlin
PullToRefreshContainer(refreshing = isRefreshing, onRefresh = { reloadData() }) {
    LazyColumn { /* ... */ }
}
```

---

## ReactionBar

**Location:** `app/src/main/java/com/android/tripbook/posts/ReactionBar.kt`

### Purpose
`ReactionBar` is a composable that displays a row of emoji reactions (e.g., 👍, ❤️, 😂, 🔥, 😢). Users can tap an emoji to select or change their reaction to a post.

### Usage
- Pass the currently selected reaction as `currentReaction` (or `null` if none).
- Provide an `onReactionSelected` callback to handle reaction changes.
- Optionally, use the `modifier` parameter for layout customization.

**Example:**
```kotlin
ReactionBar(
    currentReaction = userReaction,
    onReactionSelected = { emoji -> updateReaction(emoji) }
)
```

---

## Summary
- **PullToRefreshContainer**: Adds swipe-to-refresh to scrollable content.
- **ReactionBar**: Lets users select emoji reactions for posts.

Both components are designed for easy integration into Jetpack Compose UIs in the TripBook app.

