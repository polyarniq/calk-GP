# CalkGP — Калькулятор государственной пошлины РФ

> [English version below](#calkgp--russian-state-duty-calculator)

Android-приложение для расчёта государственной пошлины на основании главы 25.3 Налогового кодекса Российской Федерации.

## Скриншоты

| Главный экран | Расчёт пошлины |
|:---:|:---:|
| ![Главный экран](readme/screen_home.jpg) | ![Расчёт](readme/screen_calculate.jpg) |

| Результат | О приложении |
|:---:|:---:|
| ![Результат](readme/screen_result.jpg) | ![О приложении](readme/screen_about.jpg) |

## Возможности

- **Суды общей юрисдикции** — ст. 333.19 НК РФ: имущественные иски, судебные приказы, расторжение брака, апелляции, кассации и другие виды заявлений
- **Арбитражные суды** — ст. 333.21 НК РФ: имущественные иски, банкротство, судебные приказы, жалобы
- **Нотариальные действия** — ст. 333.24–333.26 НК РФ: доверенности, завещания, наследство, удостоверение сделок
- **Регистрация** — ст. 333.33 НК РФ: ЗАГС (брак, развод, смена имени), Росреестр (права, ипотека, ДДУ, земельные участки), ФНС (ООО, ИП, ликвидация)
- Копирование результата в буфер обмена
- Ссылки на конкретные пункты НК РФ

## Установка

Скачайте последний APK из [релизов](https://github.com/polyarniq/calk-GP/releases) или соберите из исходников:

```bash
./gradlew assembleDebug
```

APK будет создан в `app/build/outputs/apk/debug/`.

## Требования

- Android 8.0 (API 26) и выше
- Kotlin, Jetpack Navigation

---

# CalkGP — Russian State Duty Calculator

Android application for calculating Russian state duties (государственная пошлина) based on Chapter 25.3 of the Tax Code of the Russian Federation.

## Screenshots

| Home Screen | Duty Calculation |
|:---:|:---:|
| ![Home](readme/screen_home.jpg) | ![Calculate](readme/screen_calculate.jpg) |

| Result | About |
|:---:|:---:|
| ![Result](readme/screen_result.jpg) | ![About](readme/screen_about.jpg) |

## Features

- **Courts of General Jurisdiction** — Art. 333.19 Tax Code: property claims, court orders, divorce, appeals, cassation complaints, and other filings
- **Arbitration Courts** — Art. 333.21 Tax Code: property claims, bankruptcy, court orders, complaints
- **Notarial Actions** — Art. 333.24–333.26 Tax Code: powers of attorney, wills, inheritance, deal certification
- **Registration** — Art. 333.33 Tax Code: Civil Registry (marriage, divorce, name change), Rosreestr (property rights, mortgages, equity agreements, land plots), Federal Tax Service (LLC, sole proprietorship, liquidation)
- Copy result to clipboard
- References to specific articles of the Russian Tax Code

## Installation

Download the latest APK from [Releases](https://github.com/polyarniq/calk-GP/releases) or build from source:

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/`.

## Requirements

- Android 8.0 (API 26) or higher
- Kotlin, Jetpack Navigation

## Disclaimer

The application is for informational purposes only and does not constitute legal advice. For the exact duty amount, please refer to the current text of the Tax Code of the Russian Federation.

## Author

polyarniq — polyarniq@yandex.ru

## License

© 2026 polyarniq. All rights reserved.
