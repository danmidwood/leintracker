# Lein Tracker

If your lein dependencies are out of date then I will tell you so. Don't be lazy people.

I made this while attending Hacked in London. It's been tested in the narrowest of use cases, but it seems happy. It reads Leiningen project.clj files from the internet, that should probably be scary, but I haven't been able to break it yet.

Go check it out on a few choice Leiningen users

http://leintracker.danmidwood.com/user/danmidwood
http://leintracker.danmidwood.com/user/technomancy
http://leintracker.danmidwood.com/user/cemerick

### Limitations
No pagination on the Github repo list calls. Any users with > 100 will have the list truncated.
Clojure repos without Leiningen project files are still selectable, but cannot be examined.
And many more...


Copyright © 2013 Dan Midwood

Distributed under the Eclipse Public License, the same as Clojure.
