(ns quil.gui-core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [ttt.core :refer :all]
            [ttt.board :refer :all]
            [ttt.ai :refer :all]
            [ttt.game-master :refer :all]
            [games.saved-games :as saved]
            [games.h2 :as h2]
            [games.mysql :as sql]))


(defmethod play-again :gui [state]
  (-> state
      (assoc :status :user-setup)
      (assoc :message-key :board-setup)
      (assoc :console :gui)
      (assoc :users nil)
      (assoc :key-stroke nil)
      (assoc :enter-key? false)
      (assoc :current-player :player1)
      (assoc :player1 {:player-num 1 :piece "X"})
      (assoc :player2 {:player-num 2 :piece "O"})
      (assoc :board [0 1 2 3 4 5 6 7 8])
      (assoc :ai-turn false)
      (assoc :boxes nil)
      (assoc :turn nil)
      (assoc :game-over false)
      (assoc :winner nil)
      (assoc :pause 0)))

(defmethod restart :gui [state]
  (let [sql-game (sql/load-game (:db state) state)
        last-sql-game (assoc sql-game :old-console (:console sql-game) :console (:console state))
        filed-game (saved/pull-game)
        last-filed-game (assoc filed-game :old-console (:console filed-game) :console (:console state))
        h2-game (h2/get-last-db-game (:table state))
        last-h2-game (assoc h2-game :old-console (:console h2-game) :console (:console state))]
  ;(-> state
  ;    (assoc :status (:status last-game))
  ;    (assoc :console (:console state))
  ;    (assoc :board-size (int (Math/sqrt (count (:board last-game)))))
  ;    (assoc :users (:users last-game))
  ;    (assoc :current-player (:current-player last-game))
  ;    (assoc :player1 (:player1 last-game))
  ;    (assoc :player2 (:player2 last-game))
  ;    (assoc :board (:board last-game))
  ;    (assoc :empty-board (vec (range (count (:board last-game)))))
  ;    (assoc :depth (:depth last-game))
  ;    (assoc :level (:level last-game))
  ;    (assoc :message-key :nil)
  ;    (assoc :winner nil)
  ;    (assoc :game-count (:game-count last-game)))
   last-sql-game))



(defmulti user-message :status)
(defmulti mouse-clicked (fn [state _] (:status state)))
(defmulti key-typed (fn [state _] (:status state)))
(defmulti draw-user-prompt (fn [state & _] (:status state)))
(defmulti draw-piece (fn [state _ _] (:current-player state)))



