# java-postgis-example
Simple example of using Java, PostGIS, and OpenLayers 3

Create this table for use with this project:

    CREATE TABLE points_of_interest
            (
              gid SERIAL NOT NULL,
              name CHARACTER VARYING(240),
              the_geom GEOMETRY,
              CONSTRAINT points_of_interest_pkey PRIMARY KEY (gid),
              CONSTRAINT enforce_dims_geom CHECK (st_ndims(the_geom) = 2),
              CONSTRAINT enforce_geotype_geom CHECK (geometrytype(the_geom) = 'POINT'::TEXT OR the_geom IS NULL),
              CONSTRAINT enforce_srid_geom CHECK (st_srid(the_geom) = 4326)
            )
            WITH (
              OIDS=FALSE
            );
            CREATE INDEX points_of_interest_geom_gist
              ON points_of_interest
              USING GIST
              (the_geom);
              
See bootstrap.py from https://github.com/nileshk/flask-postgis-example/ for example of table creation and sample data.
