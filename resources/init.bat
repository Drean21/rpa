@echo off
setlocal EnableDelayedExpansion
chcp 65001 > nul
set CHROME_VERSION=121.0.6167.85
set CHROME_DIR=chrome
set CHROMEDRIVER_DIR=chromedriver
set TEMPLATE_DIR=..\template
set DOWNLOAD_URL_CHROME=https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/!CHROME_VERSION!/win64/chrome-win64.zip
set DOWNLOAD_URL_CHROMEDRIVER=https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/!CHROME_VERSION!/win64/chromedriver-win64.zip

echo "正在创建目录..."
:: 创建目录
if not exist "!CHROME_DIR!" mkdir "!CHROME_DIR!"
if not exist "!CHROMEDRIVER_DIR!" mkdir "!CHROMEDRIVER_DIR!"
if not exist "!TEMPLATE_DIR!\browser\chrome-win64" (
    set hasBrowser=0
    mkdir "!TEMPLATE_DIR!\browser"
)
if not exist "!TEMPLATE_DIR!\browser\drivers" (
    set hasBrowserDriver=0
    mkdir "!TEMPLATE_DIR!\browser\drivers"
)
echo "检查目标路径是否存在..."
:: 检查目标路径是否存在
if "!hasBrowser!" neq "0" (
    echo "目标路径 '!TEMPLATE_DIR!\browser\chrome-win64' 已存在。是否继续？（y/n）"
    set /p choice=
    if /i "!choice!" equ "y" (
        call :downloadBrowser
    ) else ( echo "你选择了不覆盖")
) else ( call :downloadBrowser )

if "!hasBrowserDriver!" neq "0" (
    echo "目标路径 '!TEMPLATE_DIR!\browser\drivers\chromedriver.exe' 已存在。是否继续？（y/n）"
    set /p choice=
    if /i "!choice!" equ "y" (
        call :downloadBrowserDrive
    ) else ( echo "你选择了不覆盖")
) else ( call :downloadBrowserDrive )
goto end_script




:downloadBrowser
    echo "正在下载Chrome..."
    :: 下载Chrome
    powershell -command "& {Invoke-WebRequest '!DOWNLOAD_URL_CHROME!' -OutFile '!CHROME_DIR!\chrome.zip'}"
    echo "正在解压Chrome..."
    powershell -command "& {Expand-Archive -Path '!CHROME_DIR!\chrome.zip' -DestinationPath '!CHROME_DIR!' -Force}"
    :: 移动Chrome到模板目录
    echo "正在将Chrome移动到 '!TEMPLATE_DIR!\browser\'..."
    move /Y "!CHROME_DIR!\chrome-win64" "!TEMPLATE_DIR!\browser\"
goto :eof

:downloadBrowserDrive
    echo "正在下载ChromeDriver..."
    :: 下载ChromeDriver
    powershell -command "& {Invoke-WebRequest '!DOWNLOAD_URL_CHROMEDRIVER!' -OutFile '!CHROMEDRIVER_DIR!\chromedriver.zip'}"
    echo "正在解压ChromeDriver..."
    powershell -command "& {Expand-Archive -Path '!CHROMEDRIVER_DIR!\chromedriver.zip' -DestinationPath '!CHROMEDRIVER_DIR!' -Force}"
    :: 移动ChromeDriver到模板目录
    echo "正在将ChromeDriver移动到 '!TEMPLATE_DIR!\browser\drivers'..."
    move /Y "!CHROMEDRIVER_DIR!\chromedriver-win64\chromedriver.exe" "!TEMPLATE_DIR!\browser\drivers"
goto :eof


:end_script
echo "Chrome和ChromeDriver已成功下载、解压并移动。"
echo "正在清理..."
:: 删除下载和解压的文件
del /Q "!CHROME_DIR!\chrome.zip"
del /Q "!CHROMEDRIVER_DIR!\chromedriver.zip"
rmdir /Q /S "!CHROME_DIR!"
rmdir /Q /S "!CHROMEDRIVER_DIR!"

echo "清理完成。"
pause