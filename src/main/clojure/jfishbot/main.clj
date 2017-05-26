(ns jfishbot.main
  (:import
    (java.awt Robot)
    (ddf.minim Minim AudioPlayer AudioRecorder AudioInput)
    (ddf.minim.analysis BeatDetect FFT)
    (java.util.regex Pattern)
    (java.awt.event KeyEvent InputEvent)
    (com.sun.jna.platform.win32 WinDef WinDef$RECT WinDef$HWND WinDef$POINT)
    (com.sun.jna Native Pointer)
    (com.sun.jna.win32 StdCallLibrary)
    (java.awt.image BufferedImage)
    (javax.imageio ImageIO)
    (java.io File)
    (jfishbot User32 Gdi32 User32$CURSORINFO User32$ICONINFOEX)
    )
  (:require
    [clojure.reflect :as r]
    [clojure.java.io :as io])
  (:use clj-return-from.core)
  )

(defmacro for-loop [[sym init check change :as params] & steps]
  `(loop [~sym ~init value# nil]
     (if ~check
       (let [new-value# (do ~@steps)]
         (recur ~change new-value#))
       value#)))

(defn genmavencmd
  []
  (let [files (file-seq (io/file "d:/workspace/projects/jfishbot/lib/"))
        cmd "mvn deploy:deploy-file -Durl=file://d:/maven_repo -Dfile=%s -DgroupId=local -DartifactId=%s -Dpackaging=jar -Dversion=1.0"
        dep "[local/%s \"1.0\"]"]
    (doseq [f files]
      (println (format cmd (.getName f) (.replaceFirst (.getName f) "[.][^.]+$" "")))
      (println (format dep (.replaceFirst (.getName f) "[.][^.]+$" ""))))))

(definterface IProcessing
  (^String sketchPath [^String fileName])
  (^java.io.InputStream createInput [^String fileName]))

(defn create-processing
  []
  (reify IProcessing
    (sketchPath [this fileName]
      fileName)
    (createInput [this fileName]
      (io/input-stream fileName))))

(defn testminim2
  "test"
  []
  (let [minim (new Minim (create-processing))
        fileplayer (.loadFile minim (.getAbsolutePath (new File (.toURI (io/file "d:/muz/lc/dahak_-_the_aftermath_instrumental_8bit_cat_id_262425099.mp3_mp3dor.ru.mp3")))))]
    (.play fileplayer)
    (Thread/sleep 5000)
    (.close fileplayer)
    (.stop minim)))

(defn testminimbd
  "test"
  []
  (let [minim (new Minim (create-processing))
        in (.getLineIn minim Minim/STEREO 2048)
        beatdetect (new BeatDetect)]
    (.debugOn minim)
    (.setSensitivity beatdetect 100)
    (let [future (future (while (not (Thread/interrupted)) (do (.detect beatdetect (.mix in)) (println (.isOnset beatdetect)) (Thread/sleep 20))))]
      (Thread/sleep 20000)
      (future-cancel future))
    (.close in)
    (.stop minim)))

(defonce minim (new Minim (create-processing)))

(defonce in (.getLineIn minim Minim/STEREO 2048))

(defn closeminim
  []
  (.close in)
  (.stop minim))

(defn listen
  [fun]
  (println "start listen")
  (let [startTime (System/currentTimeMillis)]
    (block loopb (while (< (- (System/currentTimeMillis) startTime) 20000)
                   (do (Thread/sleep 20)
                       (if (< 4 (* 1000 (.level (.mix in)))) (do (println "sound") (fun) (return-from-loopb nil)))
                       )))))

(defn testminimrecord
  "test"
  []
  (let [minim (new Minim (create-processing))
        in (.getLineIn minim)
        recorder  (.createRecorder minim in "myrecording.wav")]
    (.debugOn minim)
    (.beginRecord recorder)
    (Thread/sleep 5000)
    (.endRecord recorder)
    (.save recorder)
    ;;(.close in)
    (.stop minim)))


(defn getactivewindow
  []
  (let [buffer (char-array 2048)
        hwnd (.GetForegroundWindow com.sun.jna.platform.win32.User32/INSTANCE)
        rect (new WinDef$RECT)]
    (.GetWindowText com.sun.jna.platform.win32.User32/INSTANCE hwnd buffer 1024)
    (.GetWindowRect com.sun.jna.platform.win32.User32/INSTANCE hwnd rect)
    ;;(println "rect = " rect)
    ;;(println "Active window title=" (Native/toString buffer))
    {:title (Native/toString buffer) :size rect}))

(defn call-method* [obj m & args]
  (eval `(. ~obj ~(symbol m) ~@args)))

(def robot
  (new Robot))

(def flag true)

(def defaultsleep 2000)

(defn movemouse
  [x y]
  (.mouseMove robot x y))

(defn movemouse2
  [x y]
  (.SetCursorPos User32/INSTANCE x y))

(def lastfoundcursor (atom :idle))

(def w 800)

(def h 600)

(def wi 32)

(def hi 32)

(def hand-cursor (ImageIO/read (io/file (io/resource "hand.bmp"))))

(def arrow-cursor (ImageIO/read (io/file (io/resource "arrow.bmp"))))

(def text-cursor (ImageIO/read (io/file (io/resource "text.bmp"))))

(def bobber-cursor (ImageIO/read (io/file (io/resource "bobber1.bmp"))))

(def glove-cursor (ImageIO/read (io/file (io/resource "hand1.bmp"))))

(def sword-cursor (ImageIO/read (io/file (io/resource "sword.bmp"))))

(defn compareimgs
  [a b]
  (block loopb
         (for-loop [x 0 (< x wi) (+ x 1)]
                   (for-loop [y 0 (< y hi) (+ y 1)]
                             ;;(println (.getRGB a x y) (.getRGB b x y))
                             (if (not= (.getRGB a x y) (.getRGB b x y)) (return-from-loopb false)))) true))

(defn geticoninfo
  "test"
  []
  (let [user32 User32/INSTANCE
        cursorinfo (new User32$CURSORINFO)
        piconinfoex (new User32$ICONINFOEX)]
    (set! (. cursorinfo cbSize) (.size cursorinfo))
    (.GetCursorInfo user32 cursorinfo)
    (set! (. piconinfoex cbSize) (.size piconinfoex))
    (.GetIconInfoExW user32 (. cursorinfo hCursor) piconinfoex)
    cursorinfo))

(defn getcursor
  []
  (let [image (new BufferedImage wi hi BufferedImage/TYPE_BYTE_BINARY)
        user32 User32/INSTANCE
        gdi32 Gdi32/INSTANCE
        cursorinfo (new User32$CURSORINFO)
        hdc (.CreateCompatibleDC gdi32 Pointer/NULL)
        bitmap (.CreateCompatibleBitmap gdi32 hdc wi hi)
        point (new WinDef$POINT)]
    ;;(.ShowCursor user32 false)
    ;;(.ShowCursor user32 true)
    ;;(.GetCursorPos user32 point)
    ;;(movemouse2 (. point x) (. point y))
    ;;(set! (. cursorinfo flags) 0)
    (set! (. cursorinfo cbSize) (.size cursorinfo))
    (.GetCursorInfo user32 cursorinfo)
    (.SelectObject gdi32 hdc bitmap)
    ;;(println (.hashCode (. cursorinfo hCursor)))
    (.DrawIconEx user32 hdc 0 0 (. cursorinfo hCursor) 0 0 0 Pointer/NULL User32/DI_NORMAL)
    (for-loop [x 0 (< x wi) (+ x 1)]
              (for-loop [y 0 (< y hi) (+ y 1)]
                        (.setRGB image x y (.GetPixel gdi32 hdc x y))))
    (.DeleteObject gdi32 bitmap)
    (.DeleteDC gdi32 hdc)
    (set! (. cursorinfo hCursor) nil)
    image))

(defn getcursor2
  "not working"
  []
  (let [image (new BufferedImage wi hi BufferedImage/TYPE_BYTE_BINARY)
        user32 User32/INSTANCE
        gdi32 Gdi32/INSTANCE
        hdc (.CreateCompatibleDC gdi32 Pointer/NULL)
        bitmap (.CreateCompatibleBitmap gdi32 hdc wi hi)]
    (.SelectObject gdi32 hdc bitmap)
    (.DrawIconEx user32 hdc 0 0 (.GetCursor user32) 0 0 0 Pointer/NULL User32/DI_NORMAL)
    (for-loop [x 0 (< x wi) (+ x 1)]
              (for-loop [y 0 (< y hi) (+ y 1)]
                        (.setRGB image x y (.GetPixel gdi32 hdc x y))))
    (.DeleteObject gdi32 bitmap)
    (.DeleteDC gdi32 hdc)
    image))

(defn saveimage
  [image]
  (let [file (new File (str (.getTime (new java.util.Date)) ".bmp"))]
    (ImageIO/write ^BufferedImage image "bmp" file)))

(def scanstep 10)

(defn scanscreen
  []
  (println "start scanscreen")
  (block loopb
         (for-loop [i 280 (and (<= i 600) (.contains (:title (getactivewindow)) "Warcraft")) (+ i scanstep)]
                   (for-loop [j 200 (<= j 350) (+ j scanstep)]
                             (movemouse2 i j)
                             (Thread/sleep 20)
                             (getcursor)
                             ;;(println i j)
                             (let [currcursor (getcursor)]
                               (if (compareimgs currcursor bobber-cursor)
                                 (do (println i j " found bobber")
                                     ;;(saveimage currcursor)
                                     (return-from-loopb :bobber))))
                             ))))

(defn presslmkwithmodifier
  [key]
  (.keyPress robot key)
  (.mousePress robot InputEvent/BUTTON1_DOWN_MASK)
  (.mouseRelease robot InputEvent/BUTTON1_DOWN_MASK)
  (.keyRelease robot key))

(defn presskey
  ([key]
   (.keyPress robot key)
   (.keyRelease robot key))
  ([mod key]
   (.keyPress robot mod)
   (.keyPress robot key)
   (.keyRelease robot key)
   (.keyRelease robot mod))
  ([mod secondmod key]
   (.keyPress robot mod)
   (.keyPress robot secondmod)
   (.keyPress robot key)
   (.keyRelease robot key)
   (.keyRelease robot secondmod)
   (.keyRelease robot mod)))

(def actionslist [
                  #(println "start")
                  #(presskey KeyEvent/VK_1) ;;start fishing
                  #(let [fut (future (scanscreen))] ;;start scanning screen for bobber in background, listen for sound
                     (listen (fn[] (deref fut) (Thread/sleep 1000) (presslmkwithmodifier KeyEvent/VK_SHIFT) (Thread/sleep defaultsleep)))) ;;on sound: wait for scan to complete, loot

                  ])

(defn mainloop
  []
  (future (do
            (Thread/sleep defaultsleep)
            (while flag (doseq [f actionslist]
                            (if (.contains (:title (getactivewindow)) "Warcraft") (f) (do (println "end") (throw (Exception. "message")))))))))