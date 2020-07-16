import os
import os.path
import io
import base64
from io import BytesIO
import psycopg2
import matplotlib
matplotlib.use('Agg')
from collections import OrderedDict
import matplotlib.pyplot as plt
import squarify
from operator import itemgetter
import numpy as np
import folium
from folium.plugins import MarkerCluster

import tornado
import tornado.ioloop
import tornado.web
import tornado.httpserver
import tornado.websocket
import tornado.options
from tornado.options import define, options


connection_string="dbname='crime_news' user='************' host='localhost' port=5432 password='***************'"
conn = psycopg2.connect(connection_string)
cur=conn.cursor()

def month_to_str(month):
    switcher = {
        "1": "January",
        "2": "February",
        "3": "March",
        "4": "April",
        "5": "May",
        "6": "June",
        "7": "July",
        "8": "August",
        "9": "September",
        "10": "October",
        "11": "November",
        "12": "December"
    }
    return switcher.get(month, "Invalid month")

def month_to_int(month):
    switcher = {
        "January": "1",
        "February": "2",
        "March": "3",
        "April": "4",
        "May": "5",
        "June": "6",
        "July": "7",
        "August": "8",
        "September": "9",
        "October": "10",
        "November": "11",
        "December": "12"
    }
    return switcher.get(month, "Invalid month")

def replace_tag(from_tag):
    switcher = {
        "furto": "theft",
        "frode": "fraud",
        "truffa": "fraud",
        "sequestro": "kidnapping",
        "aggressione": "attack",
        "evasione": "evasion",
        "spaccio": "drug dealing",
        "droga": "drug dealing",
        "omicidio": "murder",
        "maltrattamento": "abuse",
        "rapina": "robbery",
        "violenza sessuale": "sexual violence",
        "riciclaggio": "money laundering"
    }
    return switcher.get(from_tag, "Invalid tag")

class MainHandler(tornado.web.RequestHandler):
    def get(self):
        request = self.get_argument("request", default=None, strip=False)
        # print(" --- request headers: " + str(self.request.headers))

        #
        # GetCrimes
        #
        if (request == None or request == "GetCrimes"):
            # OpenStreetMap - default
            # Mapbox Bright (Limited levels of zoom for free tiles)
            # Mapbox Control Room (Limited levels of zoom for free tiles)
            # Stamen (Terrain, Toner, and Watercolor)
            # Cloudmade (Must pass API key)
            # Mapbox (Must pass API key)
            # CartoDB (positron and dark_matter)
            m = folium.Map(location=[44.647114, 10.925244], tiles="CartoDB positron", zoom_start=11, height=520)
            marker_cluster = MarkerCluster().add_to(m)
            cur.execute("select extract(month from (select max(date_event) \
												    from crime_news.news)) as month, \
								extract(year from (select max(date_event) \
												   from crime_news.news)) as year")
            results=cur.fetchone()
            max_month = str(results[0]).replace('.0','')
            max_year = str(results[1]).replace('.0','')
            cur.execute("select url, title, description, text, date_event, newspaper, tag, ST_AsText(geom) \
                         from crime_news.news \
                         where extract(month from date_event) = "+max_month+" and extract(year from date_event) = "+max_year)
            results=cur.fetchall()
            tags_dict = {}
            count = 0
            for r in results:
                tag = replace_tag(str(r[6]))
                if tag not in tags_dict:
                    tags_dict[tag] = 1
                else:
                    value = tags_dict[tag] + 1
                    tags_dict[tag] = value
                if (r[7] != None):
                    geom = str(r[7]).replace('POINT(', '').replace(')','')
                    lon, lat = geom.split(' ')
                    if (float(lat) >= 44.0542 and float(lat) <= 44.9613 and float(lon) >= 10.5444 and float(lon) <= 11.3864):
                        count = count + 1
                        # allowed colors = {'beige', 'black', 'blue', 'cadetblue', 'darkblue', 'darkgreen', 'darkpurple', 'darkred', 
        				# 'gray', 'green', 'lightblue', 'lightgray', 'lightgreen', 'lightred', 'orange', 'pink', 'purple', 'red', 'white'}
                        folium.Marker(
                            location=[lat, lon],
                            popup=folium.Popup("<a href="+str(r[0])+" target=\"_blank\">"+ str(r[1]).replace('.','').strip() + "</a><br>" + str(r[4]) + "<br><b>" + tag + "</b>", max_width=200),
                            icon=folium.Icon(color='black',icon='info-sign')
                        ).add_to(marker_cluster)
            m.save('map.html')


            # plt.figure(figsize=(50,2))
            # fig, ax = plt.subplots()
            # colors = ['#191970', '#001CF0', '#0038E2', '#0055D4', '#0071C6', '#008DB8', '#00AAAA',
                      # '#00C69C', '#00E28E', '#00FF80', '#75fab8']
            # plt.rc('font', size=14)
            # squarify.plot(sizes=tags_values[::-1], label=tags_key[::-1], color=colors, alpha=0.1)
            # plt.axis('off')
            # tmpfile = BytesIO()
            # fig.savefig(tmpfile, format='png')
            # encoded = base64.b64encode(tmpfile.getvalue()).decode('utf-8')
            # html = '<img src=\'data:image/png;base64,{}\' width="270" height="200">'.format(encoded)
            # with open('piechart.html','w') as f:
                # f.write(html)

            keys = []
            sum_values = 0
            for k, v in tags_dict.items():
                keys.append(k)
                sum_values = sum_values + int(v)
            max_month = month_to_str(max_month)
            self.render("show_crimes.html", map="map.html", month=max_month, year=max_year, keys=keys, values=[tags_dict], sum=sum_values, count_crimes=count)
        #
        # GetCrimesMonthYear
        #
        elif (request == "GetCrimesMonthYear"):
            month = self.get_argument("month", default=None, strip=False)
            month = month_to_int(month)
            year = self.get_argument("year", default=None, strip=False)
            m = folium.Map(location=[44.647114, 10.925244], tiles="CartoDB positron", zoom_start=11, height=520)
            marker_cluster = MarkerCluster().add_to(m)
            cur.execute("select url, title, description, text, date_event, newspaper, tag, ST_AsText(geom) \
                         from crime_news.news \
                         where extract(year from date_event) = "+year+" \
						 and extract(month from date_event) = "+month)
            results=cur.fetchall()
            tags_dict = {}
            count = 0
            for r in results:
                tag = replace_tag(str(r[6]))
                if tag not in tags_dict:
                    tags_dict[tag] = 1
                else:
                    value = tags_dict[tag] + 1
                    tags_dict[tag] = value
                if (r[7] != None):
                    geom = str(r[7]).replace('POINT(', '').replace(')','')
                    lon, lat = geom.split(' ')
                    if (float(lat) >= 44.0542 and float(lat) <= 44.9613 and float(lon) >= 10.5444 and float(lon) <= 11.3864):
                        count = count + 1
                        # allowed colors = {'beige', 'black', 'blue', 'cadetblue', 'darkblue', 'darkgreen', 'darkpurple', 'darkred', 
        				# 'gray', 'green', 'lightblue', 'lightgray', 'lightgreen', 'lightred', 'orange', 'pink', 'purple', 'red', 'white'}
                        folium.Marker(
                            location=[lat, lon],
                            popup=folium.Popup("<a href="+str(r[0])+" target=\"_blank\">"+ str(r[1]).replace('.','').strip() + "</a><br>" + str(r[4]) + "<br><b>" + tag + "</b>", max_width=200),
                            icon=folium.Icon(color='black',icon='info-sign')
                        ).add_to(marker_cluster)
            m.save('map.html')
            keys = []
            sum_values = 0
            for k, v in tags_dict.items():
                keys.append(k)
                sum_values = sum_values + int(v)
            month = month_to_str(month)
            self.render("show_crimes.html", map="map.html", month=month, year=year, keys=keys, values=[tags_dict], sum=sum_values, count_crimes=count)
        #
        # Bad request
        #
        else:
            self.clear()
            self.set_status(400)
            self.finish("<html><body>Page not found</body></html>")




if __name__ == "__main__":

    application = tornado.web.Application(handlers=[
        (r"/crimemap", MainHandler), # http://localhost:9018/crimemap?request=GetCrimes
    ],
        autoreload=True, debug=True)
    port = 9018
    print('Listening on http://localhost:', port)
    application.listen(port)
    tornado.ioloop.IOLoop.instance().start()
