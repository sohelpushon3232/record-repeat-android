# Record → Repeat Bot (Android)

এটি একটি Android Studio project। অ্যাপটি Accessibility Service ব্যবহার করে আপনার **নিজের/অনুমোদিত** app বা website workflow একবার record করে পরে replay করতে পারে।

## কী কী করতে পারে

- এক app থেকে অন্য app-এ switch হওয়া record করা
- Button / link click record + replay
- Text input record + replay
- Submit button click করা
- Scroll replay করা
- Repeat count দেওয়া
- Email list দিলে প্রতি run-এ একেকটি email ব্যবহার করা
- Recording-এর সময় floating **BACK**, **HOME**, **STOP REC** button
- Running-এর সময় floating **STOP** button
- Screen awake রাখা while recording/running
- View ID / text / content description দিয়ে element খোঁজা; না পেলে recorded coordinate tap fallback

## Email list কীভাবে কাজ করে

ধরা যাক আপনি recording-এর সময় `first@example.com` লিখলেন। App-এর main screen-এ **Email used during recording** ঘরে ঠিক `first@example.com` লিখে তারপর RECORD করবেন।

রেকর্ডিংয়ে ঐ exact email text পাওয়া গেলে সেটি `${EMAIL}` placeholder হিসেবে save হবে। পরে email list-এ:

```
a@example.com
b@example.com
c@example.com
```

দিলে Run 1-এ `a@example.com`, Run 2-এ `b@example.com`, Run 3-এ `c@example.com` বসবে।

## APK বানানোর সবচেয়ে সহজ উপায়

1. PC/Mac-এ **Android Studio Quail 4 (2026.1.4) বা compatible newer version** install করুন।
2. এই ZIP extract করুন।
3. Android Studio → **Open** → `android_record_repeat_bot` folder নির্বাচন করুন।
4. Gradle sync হতে দিন। Project AGP 9.4.0 / Gradle 9.6 compatible configuration ব্যবহার করে।
5. SDK Manager থেকে **Android SDK 36** install থাকলে ভালো।
6. Menu: **Build → Build App Bundle(s) / APK(s) → Build APK(s)**।
7. Debug APK সাধারণত পাবেন: `app/build/outputs/apk/debug/app-debug.apk`
8. APK ফোনে পাঠিয়ে install করুন। Unknown app install permission চাইলে আপনার file manager/browser-এর জন্য allow করুন।

## ফোনে A–Z ব্যবহার

1. App install করে খুলুন।
2. **Enable Accessibility Service** চাপুন।
3. Android Settings-এ `Record Repeat Bot` খুঁজে **ON** করুন।
4. App-এ ফিরে আসুন।
5. Email variable ব্যবহার করলে recording-এ যে email দেবেন সেটি **Email used during recording** ঘরে লিখুন।
6. **RECORD ONCE** চাপুন।
7. এখন আপনার workflow হাতে একবার করুন: App A → click/type → App B → click/type → Submit ইত্যাদি।
8. Android system Back/Home দরকার হলে floating `BACK` / `HOME` button ব্যবহার করুন—তাহলেই action workflow-এ save হবে।
9. শেষ হলে floating **STOP REC** চাপুন।
10. Main app-এ গিয়ে repeat count দিন।
11. চাইলে email list-এ one email per line দিন।
12. **RUN AUTOMATICALLY** চাপুন।
13. Bot recorded steps replay করবে। বন্ধ করতে floating **STOP** চাপুন।

## গুরুত্বপূর্ণ সীমাবদ্ধতা

- ফোন **unlocked** রাখতে হবে।
- CAPTCHA, OTP, biometric prompt, banking protected screen বা anti-bot/security control bypass করা হয় না।
- Password field ইচ্ছাকৃতভাবে record করা হয় না।
- কিছু game, WebView, custom-rendered app বা manufacturer UI Accessibility node expose না করলে automation কম reliable হতে পারে। Coordinate fallback আছে, কিন্তু screen layout বদলালে coordinate ভুল হতে পারে।
- Android/OEM background restrictions-এর কারণে কিছু device-এ app switching ভিন্নভাবে behave করতে পারে।
- এটি mass Gmail/Google account creation বা security controls এড়িয়ে account creation-এর জন্য নয়।

## Project structure

- `MainActivity.java` — Record/Run/Repeat UI
- `AutomationAccessibilityService.java` — recorder + playback engine
- `accessibility_service_config.xml` — accessibility capabilities
- `AndroidManifest.xml` — service registration + launchable-app visibility query

