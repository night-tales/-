# Night Tales Studio

استوديو متكامل يعمل بالذكاء الاصطناعي لإنشاء قصص مصورة، ومسموعة، ومتحركة. يوفر التطبيق تجربة احترافية متكاملة (Project-Centric Architecture) تتيح للمستخدمين توليد القصص، تصميم الشخصيات، إعداد المشاهد (Storyboard)، توليد الصور، إضافة التعليق الصوتي والموسيقى، وتصدير الفيديو النهائي بجودات متعددة لمنصات مختلفة.

[![Version](https://img.shields.io/badge/الإصدار-1.0.0-blue?style=flat-square)](https://github.com/night-tales/-/releases/latest)
[![License](https://img.shields.io/badge/الترخيص-MIT-green?style=flat-square)](LICENSE)
[![Downloads](https://img.shields.io/github/downloads/night-tales/-/total?style=flat-square)](https://github.com/night-tales/-/releases)
[![Platform](https://img.shields.io/badge/المنصة-Android-brightgreen?style=flat-square&logo=android)](https://www.android.com)

---

## 📥 تحميل التطبيق (APK)

[![تحميل التطبيق](https://img.shields.io/badge/تحميل_التطبيق-APK-brightgreen?style=for-the-badge&logo=android&logoColor=white)](https://github.com/night-tales/-/releases/latest/download/app-release.apk)

> 💡 **ملاحظة:**  
> - هذا الزر يقوم بتحميل أحدث نسخة من ملف APK مباشرة.  
> - بعد التحميل، تأكد من تفعيل خيار **"السماح بالتثبيت من مصادر غير معروفة"** على جهازك.  
> - يمكنك أيضاً تحميل الإصدارات السابقة من [صفحة الإصدارات](https://github.com/night-tales/-/releases).

---

## 📸 لقطات الشاشة

| لوحة تحكم المشروع | استوديو القصة | استوديو الصور |
|-------------------|-----------------|-------------------|
| ![لوحة التحكم](screenshots/dashboard.png) | ![استوديو القصة](screenshots/story.png) | ![استوديو الصور](screenshots/images.png) |

---

## 📑 جدول المحتويات

- [المميزات](#-المميزات)
- [التقنيات المستخدمة](#-التقنيات-المستخدمة)
- [المتطلبات](#-المتطلبات)
- [التثبيت من المصدر](#-التثبيت-من-المصدر)
- [الاستخدام](#-الاستخدام)
- [الإصدارات وسجل التغييرات](#-الإصدارات-وسجل-التغييرات)
- [المساهمة](#-المساهمة)
- [الترخيص](#-الترخيص)
- [التواصل](#-التواصل)

---

## ✨ المميزات

- لوحة تحكم شاملة للمشروع لتتبع حالة الإنجاز (القصة، المشاهد، الصوت، إلخ).
- **Story Studio:** أدوات مدعومة بالذكاء الاصطناعي لكتابة، تحسين، اختصار، وتوسيع النصوص.
- **Character Studio:** تصميم وإدارة الشخصيات لضمان تناسقها في جميع المشاهد.
- **Scene Studio:** نظام Storyboard لتقسيم القصة وتحديد زوايا الكاميرا والصوتيات.
- **Image & Voice Studios:** توليد الصور والتعليق الصوتي بأساليب سينمائية واحترافية.
- **Timeline & Export:** محرر زمني للمونتاج وتصدير الفيديو النهائي لمنصات متعددة (YouTube, TikTok, Instagram).
- واجهة مستخدم احترافية بنمط (Dark Studio UI) تدعم اللغة العربية (RTL) بشكل كامل.

---

## 🛠️ التقنيات المستخدمة

- **لغة البرمجة:** Kotlin
- **بناء الواجهات:** Jetpack Compose
- **إدارة الحالة:** ViewModel + StateFlow / SharedFlow (MVVM Architecture)
- **المعالجة غير المتزامنة:** Kotlin Coroutines
- **قاعدة البيانات:** Room / SQLite للبيانات المحلية
- **تشغيل الوسائط:** AndroidX Media3 (ExoPlayer)
- **حقن الاعتماديات:** Dagger Hilt
- **أدوات التطوير:** Android Studio

---

## 📋 المتطلبات

لتشغيل المشروع من المصدر ستحتاج إلى:

- [Android Studio](https://developer.android.com/studio)
- [JDK 17 أو أحدث](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Git](https://git-scm.com/)
- جهاز Android أو محاكي يعمل بنظام Android 8.0 (Oreo) أو أعلى.

---

## 🔧 التثبيت من المصدر

```bash
# 1. استنساخ المستودع
git clone https://github.com/night-tales/-.git

# 2. الانتقال إلى مجلد المشروع
cd -

# 3. فتح المشروع في Android Studio
#    (افتح Android Studio ثم اختر "Open an existing project" وحدد المجلد)

# 4. مزامنة الاعتمادات (Gradle)
./gradlew build

# 5. تشغيل التطبيق
#    اضغط على زر التشغيل (Run) في Android Studio بعد توصيل جهاز أو تشغيل محاكي.
```

---

## 🚀 الاستخدام

بعد تثبيت التطبيق:

1. افتح التطبيق واستعرض **مكتبة المشاريع**.
2. قم بإنشاء مشروع جديد أو اختر القوالب الجاهزة.
3. استخدم **Story Studio** لتوليد القصة وتحريرها باستخدام الذكاء الاصطناعي.
4. خصص الشخصيات والمشاهد من خلال **Character Studio** و **Scene Studio**.
5. قم بتوليد الوسائط (الصور والصوت) عبر الاستوديوهات المخصصة.
6. عاين النتيجة النهائية في **Timeline** وقم بتصدير الفيديو من **Export Studio**.

---

## 🤝 المساهمة

المساهمات مرحب بها! يرجى قراءة دليل المساهمة (`CONTRIBUTING.md`) قبل البدء.

---

## 📄 الترخيص

هذا المشروع مرخص تحت رخصة MIT.

---

## 📞 التواصل

- البريد الإلكتروني: night26tales@gmail.com
- رابط المستودع: https://github.com/night-tales/-

---

<div dir="rtl" align="center">
صُنع بحب ❤️
</div>
