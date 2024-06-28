from appium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from appium.webdriver.webdriver import AppiumOptions

# Desired capabilities
desired_caps = {
    "platformName": "android",
    "platformVersion": "14",
    "deviceName": "Pixel_3a_API_34",
    "automationName": "UiAutomator2",
    "appPackage": "com.example.angodafake",
    "appActivity": "com.example.angodafake.LoginActivity",
    "noReset": True
}

# Appium server URL
appium_url = "http://127.0.0.1:4723"

# Initialize the driver
appium_options = AppiumOptions()
appium_options.load_capabilities(desired_caps)
driver = webdriver.Remote(appium_url, options=appium_options)

try:
    # Wait for the email input field to be present
    email_field = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "com.example.angodafake:id/emailInput"))
    )
    # Enter email
    email_field.send_keys("example@gmail.com")

    # Wait for the password input field to be present
    password_field = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "com.example.angodafake:id/passEmailInput"))
    )
    # Enter password
    password_field.send_keys("123123123")

    # Wait for the login button to be clickable and click it
    login_button = WebDriverWait(driver, 10).until(
        EC.element_to_be_clickable((By.ID, "com.example.angodafake:id/btn_login_email"))
    )
    login_button.click()

    # Wait for some element that appears after a successful login to be present
    WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "com.example.angodafake:id/success_element"))
    )
    print("Login successful!")

except Exception as e:
    print(f"An error occurred: {e}")

finally:
    # Quit the driver
    driver.quit()
