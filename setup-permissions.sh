#!/bin/bash
# tputcolors

echo -e "$(tput bold)$(tput sgr 0 1)Pi4j Permission installation script$(tput sgr0)"
echo
echo    "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *"
echo -e "* $(tput setaf 2)This script will install essential permissions to work with pi4j (https://pi4j.com/)$(tput sgr0)  *"
echo -e "* It will add new users for working with hardware and udev rules for new devices.       *"
echo -e "* $(tput bold)$(tput setaf 3)WARNING:$(tput sgr0) script require root permissions to work.                                     *"
echo    "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *"
echo
read -n 1 -r -p "$(tput bold)Continue? [Y/n]$(tput sgr0) " prompt_input
echo

# Check the user input using a case statement for clarity
case "$prompt_input" in
  [yY])
    echo
    echo "Starting installation..."
    ;;
  [nN])
    echo "Aborting."
    exit 1
    ;;
  *)
    echo "Invalid input. Aborting."
    exit 1
    ;;
esac

if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root (e.g., using sudo)." 1>&2
   exit 1
fi

CURRENT_USER="$(logname)"

echo
echo -e "Checking groups..."

# SPI BLOCK
echo -en "[1] For SPI, check for 'spi' group: "
if [ "$(getent group spi)" ]; then
    echo "$(tput bold)$(tput setaf 2)Yes$(tput sgr0)"
    echo -en "[1] For SPI, check user '$CURRENT_USER' is member of 'spi' group: "
    if id -Gn "$CURRENT_USER" | grep -qw "spi"; then
        echo "$(tput bold)$(tput setaf 2)Yes$(tput sgr0)"
    else
        echo "$(tput bold)$(tput setaf 3)No$(tput sgr0)"
        echo -en "[1] Adding user '$CURRENT_USER' to 'spi' group: "
        if usermod -aG spi "$CURRENT_USER" ; then
          echo "$(tput bold)$(tput setaf 2)Done$(tput sgr0)"
        else
          echo "$(tput bold)$(tput setaf 1)Failed!$(tput sgr0)"
          echo "Adding '$CURRENT_USER' to 'spi' group failed (Exit Status $?)."
          exit 1
        fi
    fi
else
    echo "$(tput bold)$(tput setaf 3)No$(tput sgr0)"
    echo -en "[1] Adding new group 'spi': "
    if groupadd spi; then
      echo "$(tput bold)$(tput setaf 2)Done$(tput sgr0)"
      echo -en "[1] Adding user '$CURRENT_USER' to 'spi' group: "
      if usermod -aG spi "$CURRENT_USER"; then
        echo "$(tput bold)$(tput setaf 2)Done$(tput sgr0)"
      else
        echo "$(tput bold)$(tput setaf 1)Failed!$(tput sgr0)"
        echo "Adding '$CURRENT_USER' to 'spi' group failed (Exit Status $?)."
        exit 1
      fi
    else
      echo "$(tput bold)$(tput setaf 1)Failed!$(tput sgr0)"
      echo "'spi' group addition failed (Exit Status $?)."
      exit 1
    fi
fi

# I2C BLOCK
echo -en "[2] For I2C, check for 'i2c' group: "
if [ "$(getent group i2c)" ]; then
    echo "$(tput bold)$(tput setaf 2)Yes$(tput sgr0)"
    echo -en "[2] For I2C, check user '$CURRENT_USER' is member of 'i2c' group: "
    if id -Gn "$CURRENT_USER" | grep -qw "i2c"; then
        echo "$(tput bold)$(tput setaf 2)Yes$(tput sgr0)"
    else
        echo "$(tput bold)$(tput setaf 3)No$(tput sgr0)"
        echo -en "[2] Adding user '$CURRENT_USER' to 'i2c' group: "
        if usermod -aG i2c "$CURRENT_USER" ; then
          echo "$(tput bold)$(tput setaf 2)Done$(tput sgr0)"
        else
          echo "$(tput bold)$(tput setaf 1)Failed!$(tput sgr0)"
          echo "Adding '$CURRENT_USER' to 'i2c' group failed (Exit Status $?)."
          exit 1
        fi
    fi
else
    echo "$(tput bold)$(tput setaf 3)No$(tput sgr0)"
    echo -en "[2] Adding new group 'i2c': "
    if groupadd i2c; then
      echo "$(tput bold)$(tput setaf 2)Done$(tput sgr0)"
      echo -en "[2] Adding user '$CURRENT_USER' to 'i2c' group: "
      if usermod -aG i2c "$CURRENT_USER"; then
        echo "$(tput bold)$(tput setaf 2)Done$(tput sgr0)"
      else
        echo "$(tput bold)$(tput setaf 1)Failed!$(tput sgr0)"
        echo "Adding '$CURRENT_USER' to 'i2c' group failed (Exit Status $?)."
        exit 1
      fi
    else
      echo "$(tput bold)$(tput setaf 1)Failed!$(tput sgr0)"
      echo "'i2c' group addition failed (Exit Status $?)."
      exit 1
    fi
fi

# GPIO BLOCK
echo -en "[3] For GPIO, check for 'dialout' group: "
if [ "$(getent group dialout)" ]; then
    echo "$(tput bold)$(tput setaf 2)Yes$(tput sgr0)"
    echo -en "[3] For GPIO, check user '$CURRENT_USER' is member of 'dialout' group: "
    if id -Gn "$CURRENT_USER" | grep -qw "dialout"; then
        echo "$(tput bold)$(tput setaf 2)Yes$(tput sgr0)"
    else
        echo "$(tput bold)$(tput setaf 3)No$(tput sgr0)"
        echo -en "[3] Adding user '$CURRENT_USER' to 'dialout' group: "
        if usermod -aG dialout "$CURRENT_USER" ; then
          echo "$(tput bold)$(tput setaf 2)Done$(tput sgr0)"
        else
          echo "$(tput bold)$(tput setaf 1)Failed!$(tput sgr0)"
          echo "Adding '$CURRENT_USER' to 'dialout' group failed (Exit Status $?)."
          exit 1
        fi
    fi
else
    echo "$(tput bold)$(tput setaf 3)No$(tput sgr0)"
    echo -en "[3] Adding new group 'dialout': "
    if groupadd dialout; then
      echo "$(tput bold)$(tput setaf 2)Done$(tput sgr0)"
      echo -en "[3] Adding user '$CURRENT_USER' to 'dialout' group: "
      if usermod -aG dialout "$CURRENT_USER"; then
        echo "$(tput bold)$(tput setaf 2)Done$(tput sgr0)"
      else
        echo "$(tput bold)$(tput setaf 1)Failed!$(tput sgr0)"
        echo "Adding '$CURRENT_USER' to 'dialout' group failed (Exit Status $?)."
        exit 1
      fi
    else
      echo "$(tput bold)$(tput setaf 1)Failed!$(tput sgr0)"
      echo "'dialout' group addition failed (Exit Status $?)."
      exit 1
    fi
fi

# UDEV BLOCK
PWM_RULES=$(cat <<'EOF'
SUBSYSTEM=="pwm", ACTION=="add|change", PROGRAM="/bin/sh -c 'chgrp -R dialout /sys%p && chmod -R 770 /sys%p'"
EOF
)
SPI_RULES=$(cat <<'EOF'
SUBSYSTEM=="spidev", GROUP="spi", MODE="0660"
EOF
)
GPIO_RULES=$(cat <<'EOF'
SUBSYSTEM=="gpio", KERNEL=="gpiochip*", GROUP="dialout", MODE="0660"
KERNEL=="gpio", GROUP="dialout", MODE="0660"
KERNEL=="gpio*", PROGRAM="/bin/sh -c '\
          chown -R root:dialout /sys/class/gpio && chmod -R 770 /sys/class/gpio;\
          chown -R root:dialout /sys/devices/virtual/gpio && chmod -R 770 /sys/devices/virtual/gpio;\
          chown -R root:dialout /sys$devpath && chmod -R 770 /sys$devpath'"
EOF
)

echo
echo -e "Checking udev rules..."

echo -en "[1] Checking PWM: "
PWM_MATCH=$(grep -rnHi "pwm" /etc/udev/)
if [ -n "$PWM_MATCH" ]; then
  echo "$(tput bold)$(tput setaf 2)Yes$(tput sgr0)"
  echo "[1] PWM is already configured in this udev.rules files:"
  echo "$PWM_MATCH"
else
  echo "$(tput bold)$(tput setaf 3)No$(tput sgr0)"
  echo -en "[1] Installing PWM udev rules to '/etc/udev/rules.d/99-pi4j-io.rules': "
  if echo "$PWM_RULES" >> /etc/udev/rules.d/99-pi4j-io.rules; then
    echo "$(tput bold)$(tput setaf 2)Done$(tput sgr0)"
  else
    echo "$(tput bold)$(tput setaf 1)Failed!$(tput sgr0)"
    echo "Writing udev rules to '/etc/udev/rules.d/99-pi4j-io.rules' failed! (Exit Status $?)."
    exit 1
  fi
fi

echo -en "[2] Checking SPI: "
SPI_MATCH=$(grep -rnHi "spi" /etc/udev/)
if [ -n "$SPI_MATCH" ]; then
  echo "$(tput bold)$(tput setaf 2)Yes$(tput sgr0)"
  echo "[2] SPI is already configured in this udev.rules files:"
  echo "$SPI_MATCH"
else
  echo "$(tput bold)$(tput setaf 3)No$(tput sgr0)"
  echo -en "[2] Installing SPI udev rules to '/etc/udev/rules.d/99-pi4j-io.rules': "
  if echo "$SPI_RULES" >> /etc/udev/rules.d/99-pi4j-io.rules; then
    echo "$(tput bold)$(tput setaf 2)Done$(tput sgr0)"
  else
    echo "$(tput bold)$(tput setaf 1)Failed!$(tput sgr0)"
    echo "Writing udev rules to '/etc/udev/rules.d/99-pi4j-io.rules' failed! (Exit Status $?)."
    exit 1
  fi
fi

echo -en "[3] Checking GPIO: "
GPIO_MATCH=$(grep -rnHi "gpio" /etc/udev/)
if [ -n "$GPIO_MATCH" ]; then
  echo "$(tput bold)$(tput setaf 2)Yes$(tput sgr0)"
  echo "[3] GPIO is already configured in this udev.rules files:"
  echo "$GPIO_MATCH"
else
  echo "$(tput bold)$(tput setaf 3)No$(tput sgr0)"
  echo -en "[3] Installing GPIO udev rules to '/etc/udev/rules.d/99-pi4j-io.rules': "
  if echo "$GPIO_RULES" >> /etc/udev/rules.d/99-pi4j-io.rules; then
    echo "$(tput bold)$(tput setaf 2)Done$(tput sgr0)"
  else
    echo "$(tput bold)$(tput setaf 1)Failed!$(tput sgr0)"
    echo "Writing udev rules to '/etc/udev/rules.d/99-pi4j-io.rules' failed! (Exit Status $?)."
    exit 1
  fi
fi

if [ -z "$PWM_MATCH" ] || [ -z "$SPI_MATCH" ] || [ -z "$GPIO_MATCH" ]; then
  echo -ne "[4] Reloading udev rules: "
  if udevadm control --reload-rules && sudo udevadm trigger; then
    echo "$(tput bold)$(tput setaf 2)Done$(tput sgr0)"
  else
    echo "$(tput bold)$(tput setaf 1)Failed!$(tput sgr0)"
    echo "Reloading udev rules failed! (Exit Status $?)."
    exit 1
  fi
fi


echo
echo -e "$(tput bold)$(tput setaf 2)Configuration finished! You can now use pi4j as usual with user '$CURRENT_USER'.$(tput sgr0)"
echo
exit 0