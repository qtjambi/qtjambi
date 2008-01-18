//! [0]
        while (true) {
            mutex.lock();
            keyPressed.wait(mutex);
            do_something();
            mutex.unlock();
        }
//! [0]


//! [1]
        while (true) {
            getchar();
            keyPressed.wakeAll();
        }
//! [1]


//! [2]
        while (true) {
            mutex.lock();
            keyPressed.wait(mutex);
            ++count;
            mutex.unlock();

            do_something();

            mutex.lock();
            --count;
            mutex.unlock();
        }
//! [2]


//! [3]
        while (true) {
            getchar();

            mutex.lock();
            // Sleep until there are no busy worker threads
            while (count > 0) {
                mutex.unlock();
                Thread.sleep(1);
                mutex.lock();
            }
            keyPressed.wakeAll();
            mutex.unlock();          
        }
//! [3]

