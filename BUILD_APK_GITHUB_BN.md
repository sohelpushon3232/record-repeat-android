# GitHub দিয়ে APK বানানোর সহজ উপায়

এই project-এ `.github/workflows/build-apk.yml` আগে থেকেই দেওয়া আছে।

## যা করতে হবে

1. Project GitHub repository-তে upload/push করুন।
2. GitHub repository → **Actions** → **Build Android APK** খুলুন।
3. **Run workflow** চাপুন।
4. Build শেষ হলে **Artifacts** অংশে **RecordRepeatBot-APK** পাবেন।
5. Artifact download করে unzip করলে `RecordRepeatBot.apk` পাবেন।
6. APK ফোনে install করুন।

## ফোনে install

- Settings → Security/Privacy → Install unknown apps
- যে browser/file manager দিয়ে APK খুলবেন সেটিকে Allow দিন
- `RecordRepeatBot.apk` install করুন
- App খুলে **Enable Accessibility Service** চাপুন
- Record Repeat Bot service ON করুন

## গুরুত্বপূর্ণ

এই app CAPTCHA/OTP/biometric/anti-bot protection bypass করে না। Password field record করে না। নিজের বা অনুমোদিত workflow automation-এর জন্য ব্যবহার করুন।
