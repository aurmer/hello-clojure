# hello-clojure

My first Clojure+CLJS project! This is a simple app which has routing, serves up a re-frame app, and connects to a postgres database.

## Usage

As of right now, there are three endpoints.

- `/api/time` will return a timestamp from the database
- `/api/sum-2-int/X/Y/` will return the summation of X and Y
- '/' entry to the re-frame app

The re-frame app connects to each of these with a simple form UI.

## Tech Used

- Clojure
  - Jetty
  - ring
  - compojure
  - jdbc-c3p0
  - PostgreSQL db
- Clojurescript
  - Reagent
  - re-frame
  - Bulma CSS

## Learned

This was a big undertaking due to using a language for the first time
as well as each library being new. I had a head start on re-frame from
my experience using React+Redux, but there is a lot to get used to.

I found this to be challenging and motivating to continue learning Clojure.

## License

Copyright © 2020 Aubrey Snider

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
