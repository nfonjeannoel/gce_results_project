# GCE Results Project 📱

An open-source Android application for GCE (General Certificate of Education) results management, designed to provide students with easy access to their examination results.

**🌟 Currently live on Google Play Store!**

## 🌐 Multi-Platform Availability

- **📱 Android App**: This repository
- **💻 Web Version**: Available at [gce_results_website](https://github.com/nfonjeannoel/gce_results_website) (also open source!)

## ✨ Features

- 📊 View and manage GCE results
- 🎯 User-friendly interface
- 📱 Offline support
- 🔍 Search and filter functionality
- 📈 Result analytics and insights
- 🎨 Modern Material Design

## 🚀 Getting Started

### Prerequisites

- Android Studio (latest version recommended)
- Android SDK (API level 24+)
- Git

### 🔧 Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/nfonjeannoel/gce_results_project.git
   cd gce_results_project
   ```

2. **Configure your environment**
   ```bash
   cp local.properties.example local.properties
   ```

3. **Update `local.properties` with your configuration:**
   ```properties
   # Android SDK path
   sdk.dir=YOUR_ANDROID_SDK_PATH example: C\:\\Users\\YourUserName\\AppData\\Local\\Android\\Sdk
   
   # Supabase Configuration
   SUPABASE_URL=https://your-project-id.supabase.co
   SUPABASE_ANON_KEY=your_supabase_anon_key_here
   ```

4. **Get your Supabase credentials**
   - Visit your [Supabase project dashboard](https://supabase.com/dashboard)
   - Copy your Project URL and anon/public key
   - Update the values in `local.properties`

5. **Build and run**
   - Open the project in Android Studio
   - Sync the project (Gradle sync)
   - Build and run on your device/emulator

**⚠️ Security Note:** The `local.properties` file contains sensitive information and is gitignored. Never commit this file to version control.

## 🤝 Contributing

We welcome contributions from the community! Here's how you can help:

### 🐛 Reporting Issues

- Use the [GitHub Issues](https://github.com/nfonjeannoel/gce_results_project/issues) to report bugs
- Provide detailed information about the issue
- Include steps to reproduce if possible

### 💡 Suggesting Features

- Open a [Feature Request](https://github.com/nfonjeannoel/gce_results_project/issues/new) 
- Describe the feature and its benefits
- Discuss implementation approaches

### 🔧 Development Workflow

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Make your changes**
4. **Test thoroughly**
5. **Commit your changes**
   ```bash
   git commit -m "Add amazing feature"
   ```
6. **Push to your fork**
   ```bash
   git push origin feature/amazing-feature
   ```
7. **Open a Pull Request**

### 📋 Development Guidelines

- Follow Kotlin coding conventions
- Write clear, concise commit messages
- Add tests for new features
- Update documentation as needed
- Ensure builds pass before submitting PRs

## 🏗️ Architecture

- **MVVM Architecture** with LiveData and ViewModel
- **Room Database** for offline storage
- **Retrofit** for API communication
- **Supabase** as backend service
- **Material Design Components**

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI**: Android Views with Data Binding
- **Architecture**: MVVM + Repository Pattern
- **Database**: Room (SQLite)
- **Networking**: Retrofit + OkHttp
- **Backend**: Supabase
- **Navigation**: Navigation Component

## 🔄 Current Development

### TODO
- Send loading animation to home view using ViewModel
- Create shared ViewModel for details and result list
- Implement offline caching improvements
- Add more result visualization features

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

## 🌟 Support

If you find this project helpful, please consider:
- ⭐ Starring the repository
- 🐛 Reporting issues
- 🤝 Contributing to the codebase
- 📱 Downloading from the Play Store

## 📞 Contact

- **Project Link**: [https://github.com/nfonjeannoel/gce_results_project](https://github.com/nfonjeannoel/gce_results_project)
- **Web Version**: [https://github.com/nfonjeannoel/gce_results_website](https://github.com/nfonjeannoel/gce_results_website)

---

Made with ❤️ for the GCE community