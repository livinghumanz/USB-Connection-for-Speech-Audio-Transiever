# USB Connection for Speech Audio Transceiver

This repository contains code and documentation for establishing a reliable USB connection between a Linux-based In-Vehicle Infotainment (IVI) system and an Android-based smartphone to transmit and receive audio data, with a focus on speech audio for automotive applications.

## Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Project Overview
The **USB Connection for Speech Audio Transceiver** project enables seamless audio data exchange between a Linux-based IVI system and an Android smartphone via USB. Designed for automotive environments, this project ensures low-latency, reliable transmission of speech audio, critical for applications like hands-free calling, voice assistants, or in-car communication systems. The implementation leverages USB audio protocols and is optimized for embedded systems, drawing on expertise in IoT, secure software development, and automotive systems.

This project was developed as part of my exploration into automotive embedded systems, building on my 5+ years of experience at Bosch and Tata Consultancy Services in embedded systems and automotive security.

## Features
- **Reliable USB Audio Streaming**: Establishes a robust USB connection for bidirectional audio data transfer.
- **Speech-Optimized**: Tailored for low-latency speech audio, ideal for voice-based automotive applications.
- **Cross-Platform Compatibility**: Supports Linux-based IVI systems (e.g., Automotive Grade Linux) and Android smartphones.
- **Lightweight Implementation**: Designed for resource-constrained embedded environments.
- **Extensible**: Modular code structure for integrating with other IVI functionalities or audio processing pipelines.

## Prerequisites
To use or contribute to this project, ensure you have:
- **Hardware**:
  - A Linux-based IVI system (e.g., running Automotive Grade Linux or Ubuntu).
  - An Android smartphone (API level 21 or higher).
  - USB cable supporting data transfer (USB 2.0 or higher).
- **Software**:
  - Linux environment with `libusb` and `alsa-lib` installed.
  - Android SDK with USB host support.
  - Development tools: `gcc`/`g++` for Linux, Android Studio for Android.
  - Python 3.x for scripting utilities (optional).
- **Dependencies**:
  - Install `libusb-dev` and `libasound2-dev` on Linux (`sudo apt-get install libusb-dev libasound2-dev` on Debian-based systems).
  - Ensure USB debugging is enabled on the Android device.

## Installation
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/livinghumanz/USB-Connection-for-Speech-Audio-Transiever.git
   cd USB-Connection-for-Speech-Audio-Transiever
   ```
2. **Setup Linux Environment**
   - install dependency
     ```bash
     sudo apt-get update
     sudo apt-get install libusb-dev libasound2-dev
     ```
   - Set Up Android Environment:
    - Open the Android project in Android Studio.
    - Build and deploy the app to your Android device with USB debugging enabled.
   - Connect Devices:
     - Connect the Android smartphone to the Linux IVI system via USB.
     - Ensure the Android device is recognized (check with lsusb on Linux).
## Usage
1. **Run the Linux IVI Application**:
   ```bash
   ./bin/usb_audio_transceiver
   ```
## Project Structure
```text
USB-Connection-for-Speech-Audio-Transiever/
├── src/                    # Source code for Linux and Android
│   ├── linux/             # Linux USB audio handling
│   ├── android/           # Android app for USB communication
├── include/               # Header files for shared utilities
├── bin/                   # Compiled binaries
├── scripts/               # Utility scripts for setup and testing
├── logs/                  # Log files for debugging
├── docs/                  # Documentation and protocol details
├── tests/                 # Test scripts and sample audio files
├── Makefile               # Build instructions for Linux
└── README.md              # Project documentation
```
## Contributing
Contributions are welcome! To contribute:

 1. Fork the repository.
 2. Create a feature branch (git checkout -b feature/your-feature).
 3. Commit your changes (git commit -m "Add your feature").
 4. Push to the branch (git push origin feature/your-feature).
 5. Open a pull request.
