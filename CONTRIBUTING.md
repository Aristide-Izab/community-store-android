# Community Store - Team GitHub Contribution Guide

This guide explains exactly how every team member should contribute to the Community Store Android project without overwriting other students' work or causing avoidable Git/GitHub problems.

## Project at a glance

- Platform: Native Android application
- IDE: Android Studio
- Language: Java
- UI: XML
- Authentication: Firebase Authentication
- Database: Cloud Firestore
- Design: Figma
- Version control: Git and GitHub
- Package: `com.example.communitystore`

## Golden rules

1. Never develop directly on `main`.
2. Always pull the newest `main` before starting new work.
3. Create a separate branch for every feature or responsibility.
4. Check `git status` before staging and before pushing.
5. Commit only work that belongs to your task.
6. Push your branch, then create a Pull Request into `main`.
7. Do not merge your own Pull Request until another team member has checked it.
8. Do not force-push to `main`.
9. Do not delete or rewrite another student's work without discussing it first.
10. If there is a merge conflict you do not understand, stop and ask the team before choosing which code to keep.

## 1. First-time setup

### Accept the GitHub invitation
Accept the repository collaborator invitation sent by the project owner.

### Clone the repository
Open a terminal in the folder where you want the project and run:

```bash
git clone YOUR_REPOSITORY_URL
cd community-store-android
```

Open the cloned folder in Android Studio and allow Gradle to sync.

### Firebase configuration
If `google-services.json` is not stored in the repository, get the approved project copy privately from the project owner and place it here:

```text
app/google-services.json
```

Do not upload private credentials, service-account keys, keystore files, passwords or personal student information.

## 2. Add your responsibility to the README

Before beginning your assigned feature, update only your own section in `README.md` with your name, role and responsibilities. Do not replace another student's section.

Example:

```text
Name: Student Name
Role: Marketplace and Product Management
Responsibilities:
- Marketplace home
- Product listings
- Product details
- Search and filtering
```

Include that README edit in your feature branch / Pull Request.

## 3. Start every new task correctly

Always begin from an up-to-date `main` branch:

```bash
git switch main
git pull origin main
```

Then create your own branch:

```bash
git switch -c yourname/feature-name
```

Examples:

```text
aristide/onboarding
thabo/marketplace-home
lerato/cart-checkout
sam/community-board
```

Use lowercase names and short feature descriptions.

## 4. Work only on your assigned feature

Keep your changes focused. If your task is Cart and Checkout, do not redesign Authentication unless the person responsible for Authentication has agreed to it.

If two students need to edit the same Activity, layout or Firestore structure, discuss the change before coding so both people know what is changing.

## 5. Check your work before committing

Run the app and test your feature. Then run:

```bash
git status
git diff
```

Confirm that the changed files are the files you expected.

Stage the files you intend to commit. For example:

```bash
git add app/src/main/java/com/example/communitystore/ProductActivity.java
git add app/src/main/res/layout/activity_product.xml
```

If all changed files are intentional, you may use:

```bash
git add .
```

Run `git status` again before committing.

## 6. Commit your changes

Use a short description of what you actually changed:

```bash
git commit -m "Add product details screen"
```

Good examples:

```text
Add marketplace home screen
Implement cart item removal
Connect product list to Firestore
Fix checkout navigation
Update residence verification layout
```

Avoid vague commit messages such as `update`, `changes`, `stuff` or `fixed`.

## 7. Update your branch before pushing

If other students have merged work into `main` while you were coding, update your branch before making the Pull Request:

```bash
git switch main
git pull origin main
git switch yourname/feature-name
git merge main
```

If Git reports no conflicts, continue normally.

If Git reports a conflict, do not randomly choose "Accept Current" or "Accept Incoming". Read both versions, speak to the student who worked on the other code if necessary, keep the correct combined version, test the app, then commit the resolved merge.

## 8. Push your branch

Push your own branch, never `main`:

```bash
git push -u origin yourname/feature-name
```

After the first push, future pushes can normally use:

```bash
git push
```

## 9. Create a Pull Request

On GitHub:

1. Open the Community Store repository.
2. Open **Pull requests**.
3. Select **New pull request**.
4. Base branch must be `main`.
5. Compare branch must be your feature branch.
6. Give the Pull Request a clear title.
7. Explain what you changed and what you tested.
8. Ask at least one teammate to review it.

Suggested Pull Request description:

```text
What I changed:
- Added product details screen
- Added product Firestore model
- Connected product card navigation

What I tested:
- Product opens correctly
- Back navigation works
- App builds successfully

Files/areas affected:
- ProductActivity.java
- activity_product.xml
```

Only merge when the changes have been reviewed and the app still builds correctly.

## 10. After your Pull Request is merged

Return to `main` and update your local project:

```bash
git switch main
git pull origin main
```

For the next task, create a new branch from this updated `main`.

Do not keep using an old merged branch for unrelated features.

## 11. How to handle merge conflicts safely

A merge conflict means two branches changed the same area of a file and Git cannot decide which version is correct.

When this happens:

1. Do not panic and do not delete the file.
2. Identify which files conflict using `git status`.
3. Open each conflicted file in Android Studio.
4. Compare your code with the incoming code.
5. Keep the correct combined result, not automatically one entire side.
6. Remove conflict markers such as `<<<<<<<`, `=======` and `>>>>>>>` only after deciding the correct final code.
7. Run the app again.
8. Stage the resolved files and commit the merge.

If you are unsure, contact the teammate whose work conflicts with yours before committing.

## 12. Files that should not be committed

Do not intentionally commit local, generated or sensitive files such as:

```text
local.properties
.gradle/
build/
app/build/
keystore files
private keys
service-account JSON files
passwords
API secrets
personal user/student data
```

Follow the repository `.gitignore`. Do not delete `.gitignore` just because Git is ignoring a file.

For `google-services.json`, follow the team's repository policy. If it is ignored, obtain it privately and place it in the `app/` directory.

## 13. Before asking someone else to fix your Git problem

Send the team these outputs:

```bash
git status
git branch --show-current
git log --oneline -5
```

Also include the exact error message. Do not run destructive commands such as `git reset --hard` or force-push commands unless the team has agreed to them.

## Quick workflow to remember

```text
1. git switch main
2. git pull origin main
3. git switch -c yourname/feature-name
4. Code and test
5. git status
6. git add <your files>
7. git commit -m "Clear message"
8. Update from main if needed
9. git push -u origin yourname/feature-name
10. Create Pull Request
11. Get teammate review
12. Merge into main
13. git switch main
14. git pull origin main
```

## Team principle

`main` is the team's stable shared version. Feature branches are each student's workspace. Pull Requests are the checkpoint that protects everyone's work.
