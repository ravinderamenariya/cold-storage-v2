import time
import board
import digitalio
import adafruit_dht
from gpiozero import Servo

from PIL import Image, ImageDraw, ImageFont
import adafruit_rgb_display.st7735 as st7735


# ================== CONFIGURATION ==================

MQ_ACTIVE_LOW = True
RELAY_ACTIVE_LOW = True

# DHT11 sensors
dht_ch1 = adafruit_dht.DHT11(board.D17)        # GPIO17, physical pin 11
dht_ch2 = adafruit_dht.DHT11(board.D26)        # GPIO26, physical pin 37
dht_outside = adafruit_dht.DHT11(board.D22)    # GPIO22, physical pin 15

# MQ sensors (Yahan hardware wiring ke hisaab se pins ko proper map kiya hai)
mq137 = digitalio.DigitalInOut(board.D4)       # Chamber 1 Sensor (GPIO4 / Pin 7)
mq137.direction = digitalio.Direction.INPUT

mq2 = digitalio.DigitalInOut(board.D23)        # Chamber 2 Sensor (GPIO23 / Pin 16)
mq2.direction = digitalio.Direction.INPUT

# Fan relays (Chamber 1 aur Chamber 2 ke correct physical relays)
fan1 = digitalio.DigitalInOut(board.D19)       # Chamber 1 Fan (GPIO19 / Pin 35)
fan2 = digitalio.DigitalInOut(board.D16)       # Chamber 2 Fan (GPIO16 / Pin 36)

for relay in (fan1, fan2):
    relay.direction = digitalio.Direction.OUTPUT
    relay.value = RELAY_ACTIVE_LOW  # OFF for active-low relay

# ================== SERVO MOTORS CONFIGURATION ==================
servo1 = Servo(12, min_pulse_width=0.0005, max_pulse_width=0.0025)  # Chamber 1 Servo (GPIO12)
servo2 = Servo(13, min_pulse_width=0.0005, max_pulse_width=0.0025)  # Chamber 2 Servo (GPIO13)

# Servo jitter fix ke liye variables
last_angle1 = -1
last_angle2 = -1

def set_servo_angle_stable(servo_obj, angle, last_angle_ref):
    if last_angle_ref != angle:
        value = (angle / 90.0) - 1.0
        if value < -1.0: value = -1.0
        if value > 1.0: value = 1.0
        servo_obj.value = value
        return angle
    return last_angle_ref

# Shuruat me dono servos ko 5 degree par set karein
last_angle1 = set_servo_angle_stable(servo1, 5, last_angle1)
last_angle2 = set_servo_angle_stable(servo2, 5, last_angle2)


# ================== TFT DISPLAY (ST7735) ==================

spi = board.SPI()

cs_pin = digitalio.DigitalInOut(board.D6)      # Physical pin 31
dc_pin = digitalio.DigitalInOut(board.D5)      # Physical pin 29
rst_pin = digitalio.DigitalInOut(board.D25)    # Physical pin 22

display = st7735.ST7735R(
    spi,
    cs=cs_pin,
    dc=dc_pin,
    rst=rst_pin,
    baudrate=24000000,
    width=128,
    height=160,
    rotation=0
)

WIDTH = 128
HEIGHT = 160

BLACK = (0, 0, 0)
WHITE = (240, 245, 240)
GREEN = (0, 255, 100)
DARK_GREEN = (0, 100, 45)
RED = (255, 65, 65)
YELLOW = (255, 210, 0)
GREY = (150, 160, 160)
CYAN = (0, 220, 230)


def get_font(size):
    try:
        return ImageFont.truetype(
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
            size
        )
    except OSError:
        return ImageFont.load_default()


FONT_SMALL = get_font(8)
FONT_MEDIUM = get_font(10)
FONT_LARGE = get_font(15)


def show_image(image):
    display.image(image)


# ================== STARTUP ANIMATION ==================

def startup_animation():
    for progress in range(0, 101, 5):
        img = Image.new("RGB", (WIDTH, HEIGHT), BLACK)
        draw = ImageDraw.Draw(img)

        draw.text((18, 25), "VISION X", font=FONT_LARGE, fill=GREEN)
        draw.text((18, 48), "GREEN COLD", font=FONT_MEDIUM, fill=WHITE)
        draw.text((20, 85), "SYSTEM STARTING", font=FONT_SMALL, fill=CYAN)

        draw.rectangle((12, 110, 116, 120), outline=GREEN, width=1)
        fill_width = int(102 * progress / 100)
        draw.rectangle((13, 111, 13 + fill_width, 119), fill=DARK_GREEN)

        draw.text((48, 130), f"{progress}%", font=FONT_SMALL, fill=WHITE)

        show_image(img)
        time.sleep(0.04)


# ================== SENSOR DATA ==================

sensor_data = {
    "CH1": {"temp": None, "hum": None},
    "CH2": {"temp": None, "hum": None},
    "OUT": {"temp": None, "hum": None}
}


def read_dht(sensor, name):
    try:
        temp = sensor.temperature
        hum = sensor.humidity

        if temp is not None and hum is not None:
            sensor_data[name]["temp"] = temp
            sensor_data[name]["hum"] = hum

    except RuntimeError:
        pass
    except Exception as error:
        print(f"{name} sensor error: {error}")


def read_mq(sensor):
    try:
        value = sensor.value
        if MQ_ACTIVE_LOW:
            return value is False
        return value is True
    except Exception:
        return False


def format_value(value):
    if value is None:
        return "--"
    return str(round(value, 1))


# ================== DASHBOARD ==================

def draw_dashboard(gas1, gas2, f1_on, f2_on):
    img = Image.new("RGB", (WIDTH, HEIGHT), BLACK)
    draw = ImageDraw.Draw(img)

    # Header
    draw.rectangle((0, 0, 127, 19), fill=DARK_GREEN)
    draw.text((5, 4), "VISION X | GREEN COLD", font=FONT_SMALL, fill=WHITE)

    # Sensor table heading
    draw.text((4, 24), "SENSOR", font=FONT_SMALL, fill=CYAN)
    draw.text((65, 24), "TEMP C", font=FONT_SMALL, fill=CYAN)
    draw.text((101, 24), "HUM %", font=FONT_SMALL, fill=CYAN)
    draw.line((3, 36, 124, 36), fill=GREEN)

    rows = [
        ("CH1", sensor_data["CH1"]),
        ("CH2", sensor_data["CH2"]),
        ("OUT", sensor_data["OUT"])
    ]

    y = 41
    for label, data in rows:
        draw.text((5, y), label, font=FONT_MEDIUM, fill=WHITE)
        draw.text((65, y), format_value(data["temp"]), font=FONT_SMALL, fill=WHITE)
        draw.text((101, y), format_value(data["hum"]), font=FONT_SMALL, fill=WHITE)
        y += 18

    draw.line((3, 94, 124, 94), fill=GREEN)

    # Gas alerts
    draw.text((5, 99), "GAS MONITOR", font=FONT_MEDIUM, fill=CYAN)

    gas1_text = "ALERT" if gas1 else "SAFE"
    gas2_text = "ALERT" if gas2 else "SAFE"

    draw.text((5, 114), "CH1 Sensor:", font=FONT_SMALL, fill=WHITE)
    draw.text((85, 114), gas1_text, font=FONT_SMALL, fill=RED if gas1 else GREEN)

    draw.text((5, 126), "CH2 Sensor:", font=FONT_SMALL, fill=WHITE)
    draw.text((85, 126), gas2_text, font=FONT_SMALL, fill=RED if gas2 else GREEN)

    # Actuator status
    draw.line((3, 139, 124, 139), fill=GREEN)
    f1_str = "ON" if f1_on else "OFF"
    f2_str = "ON" if f2_on else "OFF"
    s1_ang = "60°" if f1_on else "5°"
    s2_ang = "60°" if f2_on else "5°"
    
    draw.text((5, 143), f"F1:{f1_str}({s1_ang}) F2:{f2_str}({s2_ang})", font=FONT_SMALL, fill=YELLOW)

    if gas1 or gas2:
        draw.text((5, 153), "WARNING - GAS DETECTED!", font=FONT_SMALL, fill=RED)
    else:
        draw.text((5, 153), "SYSTEM STATUS: ALL SAFE", font=FONT_SMALL, fill=GREEN)

    show_image(img)


# ================== MAIN PROGRAM ==================

def main():
    global last_angle1, last_angle2
    print("VISION X - GREEN COLD")
    print("Monitoring started with perfectly matched Chamber 1 & Chamber 2 logic.")

    startup_animation()

    last_dht_read = 0

    try:
        while True:
            now = time.monotonic()

            # DHT11: read approximately every 2 seconds
            if now - last_dht_read >= 2:
                read_dht(dht_ch1, "CH1")
                read_dht(dht_ch2, "CH2")
                read_dht(dht_outside, "OUT")
                last_dht_read = now

            # Read gas sensors
            gas1 = read_mq(mq137)  # Chamber 1 gas alert
            gas2 = read_mq(mq2)    # Chamber 2 gas alert

            # --- CHAMBER 1 CONTROL LOGIC (Sensor 1 -> Fan 1 & Servo 1) ---
            if gas1:
                fan1.value = not RELAY_ACTIVE_LOW  # Fan 1 ON
                last_angle1 = set_servo_angle_stable(servo1, 60, last_angle1)  # Servo 1 @ 60°
                f1_running = True
                print("WARNING: CH1 Alert -> Fan 1 ON, Servo 1 @ 60°")
            else:
                fan1.value = RELAY_ACTIVE_LOW      # Fan 1 OFF
                last_angle1 = set_servo_angle_stable(servo1, 5, last_angle1)   # Servo 1 @ 5°
                f1_running = False

            # --- CHAMBER 2 CONTROL LOGIC (Sensor 2 -> Fan 2 & Servo 2) ---
            if gas2:
                fan2.value = not RELAY_ACTIVE_LOW  # Fan 2 ON
                last_angle2 = set_servo_angle_stable(servo2, 60, last_angle2)  # Servo 2 @ 60°
                f2_running = True
                print("WARNING: CH2 Alert -> Fan 2 ON, Servo 2 @ 60°")
            else:
                fan2.value = RELAY_ACTIVE_LOW      # Fan 2 OFF
                last_angle2 = set_servo_angle_stable(servo2, 5, last_angle2)   # Servo 2 @ 5°
                f2_running = False

            # Update Display Dashboard
            draw_dashboard(gas1, gas2, f1_running, f2_running)
            time.sleep(0.5)

    except KeyboardInterrupt:
        print("\nProgram stopped.")

    finally:
        try:
            fan1.value = RELAY_ACTIVE_LOW
            fan2.value = RELAY_ACTIVE_LOW
            servo1.value = None
            servo2.value = None
        except Exception:
            pass

        for sensor in (dht_ch1, dht_ch2, dht_outside):
            try:
                sensor.exit()
            except Exception:
                pass

        for device in (mq137, mq2, fan1, fan2, cs_pin, dc_pin, rst_pin):
            try:
                device.deinit()
            except Exception:
                pass

        print("Cleanup complete.")


if __name__ == "__main__":
    main()