-- Database: posts

-- DROP DATABASE IF EXISTS posts;

CREATE DATABASE posts
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'fr-FR'
    LC_CTYPE = 'fr-FR'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

	create table utilisateur (
		id_utilisateur serial primary key,
		nom varchar(50),
		adresse varchar(50),
		sexe varchar(8),
		destination varchar(50),
		pays_d_origine varchar(50),
		photo_profil bytea,
		age serial

	);

	create table post (
		id_post serial primary key,
		libelle varchar(100),
		nbre_commentaires int,
		nbre_likes int,
		date_publication date,
		photos bytea
	);

	select * from utilisateur;
	select * from post;