signal "temperature" unit "°C" distribution normal(22.0, 3.0) noise uniform(-0.5, 0.5)
signal "humidity" unit "%" distribution normal(65.0, 10.0)
signal "pressure" unit "hPa" distribution normal(1013.25, 5.0)
procedure "gust", {
    occurrence every(minutes(2)) jitter seconds(30)
    signal "wind_speed" unit "km/h" distribution normal(45.0, 10.0)
    duration normal(15.0, 5.0)
}
