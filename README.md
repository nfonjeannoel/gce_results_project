# GCE Results Project 📱

An open-source Android application for GCE (General Certificate of Education) results management, designed to provide students with easy access to their examination results.

**🌟 Currently live on Google Play Store!**
📱 **[Download from Google Play Store](https://play.google.com/store/apps/details?id=com.camgist.gceresults&hl=en_US)**

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

## 🌳 Branching Strategy

This project follows a structured branching workflow:

### Main Branches

- **`main`** - Production-ready code. This branch contains stable releases and is protected.
- **`develop`** - Integration branch for features. All feature development merges here first.

### Feature Development Workflow

1. **Start from develop branch**
   ```bash
   git checkout develop
   git pull origin develop
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Develop your feature**
   - Make your changes
   - Test thoroughly
   - Follow coding conventions

4. **Commit your changes**
   ```bash
   git add .
   git commit -m "feat: add your feature description"
   ```

5. **Push feature branch**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create Pull Request**
   - Target the `develop` branch
   - Provide clear description
   - Request code review

7. **After PR approval and merge**
   ```bash
   git checkout develop
   git pull origin develop
   git branch -d feature/your-feature-name  # Delete local feature branch
   ```

### Release Process

1. Features are merged into `develop`
2. When ready for release, `develop` is merged into `main`
3. `main` branch triggers deployment to Google Play Store

### 📝 Commit Message Guidelines

Use conventional commit format:
- `feat:` - New features
- `fix:` - Bug fixes
- `docs:` - Documentation changes
- `style:` - Code formatting
- `refactor:` - Code restructuring
- `test:` - Adding tests
- `chore:` - Maintenance tasks

Example:
```bash
git commit -m "feat: add offline result caching"
git commit -m "fix: resolve login validation issue"
git commit -m "docs: update README with new setup instructions"
```

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