export PERSISTENCE_UNIT_NAME=my-pu
export SPRING_PROFILES_ACTIVE=prod
export DB_USER=pgdbusername
export DB_PASSWORD=pgdbpass

echo '--------- Evnrionments ----------'
echo PERSISTENCE_UNIT_NAME=$PERSISTENCE_UNIT_NAME
echo spring.profiles.active=$SPRING_PROFILES_ACTIVE
echo DB_USER=$DB_USER
echo DB_PASSWORD=$DB_PASSWORD
