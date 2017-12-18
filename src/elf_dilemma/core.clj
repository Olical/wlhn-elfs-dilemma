(ns elf-dilemma.core
  (:require [gniazdo.core :as ws]
            [cheshire.core :as cheshire]))

(def last-msg (atom nil))
(defonce socket (atom nil))

(defn respond [tag contents]
  (ws/send-msg @socket (cheshire/generate-string {:tag tag, :contents contents})))

(def behaviours ["Betray" "StayLoyal"])

(defn handle-response [msg]
  (let [tick (cheshire/parse-string msg keyword)]
    (reset! last-msg tick)
    (respond "SetColor" (str "#" (format "%x" (rand-int 16rFFFFFF))))
    (doseq [direction ["North" "South" "East" "West"]]
      (respond "MakeChoice" [(rand-nth behaviours) direction]))))

(comment
  (reset! socket (ws/connect "ws://game.clearercode.com:8000" :on-receive #'handle-response))

  (ws/close @socket))

(select-keys @last-msg #{:name :score})
