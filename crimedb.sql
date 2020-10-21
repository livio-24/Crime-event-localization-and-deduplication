CREATE DATABASE crime_news;
CREATE SCHEMA crime_news;
CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;
CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA crime_news;

CREATE TABLE crime_news.algorithm (
    id integer NOT NULL PRIMARY KEY,
    name character varying(255) NOT NULL,
    numofdays integer NOT NULL,
    configurations text NOT NULL
);

CREATE TABLE crime_news.link (
    id serial NOT NULL PRIMARY KEY,
    url character varying(255) NOT NULL unique
);

CREATE TABLE crime_news.duplicate (
    id_news1 integer NOT NULL,
    id_news2 integer NOT NULL,
    algorithm integer NOT NULL,
    value double precision NOT NULL,
    constraint duplicate_pk primary key (id_news1, id_news2, algorithm),
    constraint duplicate_algorithm_fk foreign key (algorithm) references crime_news.algorithm(id),
    constraint duplicate_link_fk1 foreign key (id_news1) references crime_news.link(id),
    constraint duplicate_link_fk2 foreign key (id_news2) references crime_news.link(id)
);

CREATE TABLE crime_news.news (
    url character varying(255) NOT NULL PRIMARY KEY,
    title character varying(255) NOT NULL,
    description text NOT NULL,
    text text NOT NULL,
    municipality character varying(255),
    area character varying(255) NOT NULL,
    address character varying(255),
    date_publication date NOT NULL,
    time_publication time without time zone NOT NULL,
    geom public.geometry(Point,4326),
    object character varying(255) NOT NULL,
    newspaper character varying(255) NOT NULL,
    tag character varying(255) NOT NULL,
    is_general integer NOT NULL,
    date_event date,
    constraint news_link_fk foreign key (url) references crime_news.link(url)
);

CREATE TABLE crime_news.entity (
    url_news varchar,
	entity varchar,
	entity_type varchar(128),
	uri varchar,
	frequency integer,
    constraint entity_pk primary key (url_news, entity, entity_type),
    constraint entity_news_fk foreign key (url_news) references crime_news.news(url)
);
