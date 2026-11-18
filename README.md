# 🎨 ComposeCraft
### **No-Code Android UI/UX Designer & Jetpack Compose Generator**
> *ডিজাইন করুন সরাসরি ফোনে কোনো কোডিং ছাড়া, দেখুন ৩ডি অর্র্ব ও লাইভ অ্যানিমেশন, আর ১-ক্লিকে নামিয়ে নিন প্রোডাকশন-রেডি Jetpack Compose কোড ও ফুল অ্যান্ড্রয়েড প্রজেক্ট।*

---

## 🌟 মূল বৈশিষ্ট্যসমূহ (Key Features)

### ১. 🔮 ৩ডি অর্র্ব ও লাইভ অ্যানিমেশন সাপোর্ট (3D Glowing AI Orb & Live Animations)
* **3D Glowing AI Orb:** বাস্তবসম্মত নিয়ন আলো, রোটেটিং ৩ডি গ্রেডিয়েন্ট এবং ব্রিদিং অর্র্ব যা ডিজাইনের ভেতর জীবন্তভাবে স্পিন ও পালস করে।
* **Live Audio Waveform:** রিয়েল-টাইম ড্যান্সিং অডিও ওয়েভবার যা ভয়েস এআই বা মিডিয়া প্লেয়ারের জন্য পারফেক্ট।
* **Pulsing Glow Container:** সাইবারপাঙ্ক স্টাইলের গ্লোয়িং বর্ডারযুক্ত কন্টেইনার।

### ২. 🎯 ডুয়াল ক্যানভাস অভিজ্ঞতা (Dual Canvas Experience)
* **Option A: Free-form Drag & Drop:** আঙুল দিয়ে সরাসরি টেনে এলিমেন্ট যেকোনো পজিশনে বসানো।
* **Option B: Structured Layer Hierarchy Tree:** Figma ও Android Studio-র মতো লেয়ার ট্রির মাধ্যমে `Column`, `Row`, `Box`, `Card` কন্টেইনারে নিখুঁত নেস্টিং।

### ৩. 👁️ লাইভ ইন্টারঅ্যাক্টিভ প্লে মোড (Interactive Play / Prototype)
* "Play" বাটনে এক ক্লিকেই আসল অ্যাপের মতো চালিয়ে দেখা:
  * বোতামে ট্যাপ করলে এক স্ক্রিন থেকে অন্য স্ক্রিনে যাওয়া।
  * ইনপুট বক্সে টাইপ করা, সুইচ ও চেকবক্স অন/অফ করা, স্লাইডার নাড়ানো।

### ৪. 💾 ডাউনলোড ও ফুল প্রজেক্ট এক্সপোর্ট (Download Complete Code for Any AI Agent)
* **১-ক্লিকে ফোন স্টোরেজে ডাউনলোড (`/sdcard/Download/ComposeCraft_...`):**
  * সম্পূর্ণ স্বয়ংসম্পূর্ণ অ্যান্ড্রয়েড প্রজেক্ট জেনারেট হয়ে যায় (`build.gradle.kts`, `AndroidManifest.xml`, `MainActivity.kt`, `ui/screens/*.kt`)।
* **🤖 AI Agent Ready (`PROMPT_FOR_AI.md`):**
  * এমনভাবে প্রম্পট তৈরি করে রাখা আছে যা যেকোনো AI Agent (Claude, Gemini, ChatGPT, Cursor, Copilot)-কে দিলে কোনো প্রকার এরর বা ঝামেলা ছাড়াই সরাসরি কোড বিল্ড ও রান করতে পারবে।

---

## 🚀 কীভাবে GitHub Actions দিয়ে ১-ক্লিকে APK তৈরি করবেন

প্রজেক্টে স্বয়ংক্রিয় **GitHub Actions Workflow** যোগ করা আছে (`.github/workflows/build.yml`)।

### ধাপসমূহ:
1. টার্মিনালে এই ফোল্ডারে গিট ইনিশিয়ালাইজ ও পুশ করুন:
   ```bash
   cd ~/projects/ComposeCraft
   git init
   git add .
   git commit -m "Initial commit of ComposeCraft"
   git branch -M main
   git remote add origin https://github.com/<your-username>/ComposeCraft.git
   git push -u origin main
   ```
2. GitHub-এ কোড পুশ হওয়ার সাথে সাথে **Actions** ট্যাবে অটোমেটিকভাবে রিলিজ APK বিল্ড হবে।
3. বিল্ড শেষ হলে **Releases** সেকশন থেকে সরাসরি `ComposeCraft-latest.apk` নামিয়ে ফোনে ইনস্টল করে নিন!

---

## 📂 প্রজেক্ট আর্কিটেকচার (Architecture)

```
ComposeCraft/
├── .github/workflows/build.yml         # অটোমেটিক ক্লাউড APK বিল্ডার
├── app/src/main/
│   ├── AndroidManifest.xml
│   └── java/com/composecraft/app/
│       ├── MainActivity.kt             # অ্যাপ এন্ট্রি পয়েন্ট
│       ├── model/
│       │   ├── UIElementType.kt        # ৩ডি অর্র্ব, ওয়েভফর্ম, বাটন, কন্টেইনার ইত্যাদি
│       │   ├── ElementProperties.kt    # কালার, সাইজ, অ্যানিমেশন স্পিড, রেডিয়াস প্রপার্টি
│       │   ├── UIElementNode.kt        # ট্রি ডেটা স্ট্রাকচার ও নোড ম্যানিপুলেশন
│       │   ├── AppScreen.kt            # মাল্টি-স্ক্রিন মডেল
│       │   ├── AppProject.kt           # ফুল প্রজেক্ট মডেল
│       │   └── Templates.kt            # AI Voice Assistant, Login, Profile, Store টেমপ্লেট
│       ├── engine/
│       │   ├── DynamicCanvasRenderer.kt# ডায়নামিক ক্যানভাস রেন্ডারার ইঞ্জিন
│       │   ├── GlowingOrbRenderer.kt   # ৩ডি নিয়ন অর্র্ব ও ওয়েভফর্ম অ্যানিমেশন ইঞ্জিন
│       │   ├── ComposeCodeGenerator.kt # প্রোডাকশন-গ্রেড Kotlin Compose কোড জেনারেটর
│       │   └── ProjectExporter.kt      # ফোন স্টোরেজে প্রজেক্ট ও ফাইল ডাউনলোডার
│       ├── viewmodel/
│       │   └── DesignerViewModel.kt    # স্টেট ও আনডু/রিডু হিস্ট্রি ম্যানেজার
│       └── ui/
│           ├── components/
│           │   ├── DeviceFrame.kt      # প্রিমিয়াম ফোন ফ্রেম (স্ট্যাটাস বার, নচ সহ)
│           │   ├── ComponentPaletteSheet.kt # উইজেট ও অ্যানিমেশন পিকার ড্রয়ার
│           │   ├── PropertyInspectorSheet.kt# কালার পিকার ও স্টাইল ইন্সপেক্টর
│           │   ├── LayerTreeDrawer.kt  # হায়ারার্কি লেয়ার ট্রি ড্রয়ার
│           │   └── CodeExportDialog.kt # কোড ভিউয়ার, AI Prompt ও ডাউনলোড ডায়ালগ
│           └── screens/
│               └── DesignerScreen.kt   # মূল ফুলস্ক্রিন ওয়ার্কস্পেস
```
