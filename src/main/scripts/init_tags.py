import json
import mysql.connector

host = "video-spot.ru:3306"
user = "spot-spring"
password = "Fsi01?$4"
database = "spot"

connection = mysql.connector.connect(
    host=host,
    user=user,
    password=password,
    database=database
)

cursor = connection.cursor()

data = json.load(open('labels_map.txt'))

i = 1
for key, value in data.items():
    query = "INSERT INTO tags (id, tag) VALUES (%s, %s)"
    values = (i, value)
    cursor.execute(query, values)
    connection.commit()
    i += 1

cursor.close()
connection.close()
