(ns quil.gui
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [quil.dimensions :as dim]
            [quil.board :as gui-board]
            [ttt.board :as board]
            [quil.button :as button]
            [quil.gui-core :refer :all]
            [ttt.core :refer :all]
            [ttt.game-setup :as setup]
            [quil.gui-messages :as msg]
            [ttt.game-master :refer :all]
            [quil.mouse-clicks :refer :all]
            [quil.human-prompts :refer :all]
            [quil.boxes :refer :all]
            [quil.game-pieces :as piece]
            [games.saved-games :as saved]
            [games.mysql-games :as sql]))


(defn setup-gui []
  (q/frame-rate 50)
  (q/set-state! :status :waiting
                :game-count 0
                :message-key :waiting
                :console :gui
                :database :mysql
                :users nil
                :board-size 3
                :board-set? false
                :key-stroke nil
                :current-player :player1
                :player1 {:player-num 1 :piece "X" :type nil}
                :player2 {:player-num 2 :piece "O" :type nil}
                :current-plyr-num 1
                :board [0 1 2 3 4 5 6 7 8]
                :rows []
                :cols []
                :diags []
                :ai-turn false
                :boxes nil
                :level :hard
                :depth 0
                :turn nil
                :played-boxes []
                :game-over false
                :play-again-pause 0
                :winner nil
                :table "TTT"))

(defn get-message-key [state]
  (cond (= (:status state) :playing) (if (= :player1 (:current-player state)) :player1 :player2)
        (= (:status state) :game-over) (cond (= (:winner state) 0) :catsgame (= (:winner state) 1) :x-won :else :o-won)
        :else (:status state)))

(defn ai-turn? [state] (and (= :playing (:status state)) (= :computer (:type ((:current-player state) state)))))

(defn get-box-count [state]
  (if (:board-set? state) (int (Math/pow (:board-size state) 2)) 3))

(defn get-player-num [state] (:player-num ((:current-player state) state)))

;(defn ai-turn [state]
;  (if (ai-turn? state)
;  (let [box (play-box state)
;        new-state (play-turn state box)]
;    (if (game-over? new-state)
;      (assoc new-state :game-over true)
;      new-state))))

(defn update-state [state]
  {:game-over        (game-over? state)
   :game-count       (:game-count state)
   :winner           (if (game-over? state) (:winner (get-winner state)))
   :player1          (:player1 state)
   :player2          (:player2 state)
   :console          (:console state)
   :database         (:database state)
   :users            (:users state)
   :board-size       (:board-size state)
   :board-set?       (:board-set? state)
   :box-count        (get-box-count state)
   :key-stroke       (:key-stroke state)
   :empty-board      (board/create-board (:board-size state))
   :board            (if (and (not (game-over? state)) (ai-turn? state)) (make-move state (play-box state)) (:board state))
   :depth            (:depth state)
   :level            (:level state)
   :box-played       (:box-played state)
   :played-boxes     (remove nil? (map #(if (not (int? %1)) %2) (:board state) (vec (range 0 (count (:board state))))))
   :turns-played     (count (:played-boxes state))
   :current-player   (if (and (not (game-over? state)) (ai-turn? state)) (next-player state) (:current-player state))
   :play-again-pause (if (:game-over state) (if (< (:play-again-pause state) 100) (inc (:play-again-pause state)) 100) 0)
   :status           (if (game-over? state) (if (= 100 (:play-again-pause state)) :play-again :game-over) (:status state))
   :message-key      (get-message-key state)
   :table            (:table state)
   :save             (if (or (ai-turn? state) (:game-over state)) (saved/save-game state))
   :save-to-db       (if (or (ai-turn? state) (:game-over state)) (sql/save-to-sql state (:table state)))})

(defn is-box-in-win? [box state]
  (let [board (:board state)
        owning-lines (get-box-lines box (:empty-board state))
        played-boxes-vectors (for [line owning-lines box line] (nth board box))
        line-wins? (map #(board/is-vector-win? %) played-boxes-vectors)
        win? (not (empty? (filter true? line-wins?)))]
    played-boxes-vectors))

(defn draw-state [state]
  (gui-board/draw-console)
  (gui-board/draw-gui-board (:board-size state))
  (button/draw-game-button state)

  (if (or (= (:status state) :user-setup) (= (:status state) :player-setup)
          (= (:status state) :board-setup) (= (:status state) :level-setup)
          (= (:status state) :restart?))
    (draw-user-prompt state))

  (doseq [box (:played-boxes state)]
    ;(let [board (:board state)]
    ;winning-line-index (board/winning-line-index board)
    ;winning-line (nth (board/get-all-lines board) winning-line-index)
    ;win? (box-in-line? box winning-line)]
    (draw-box box state false))

  (if (= (:status state) :playing) (draw-piece state (size-boxes state) [(q/mouse-x) (q/mouse-y)]))

  )


(defn -main []
  (q/defsketch gui
               :title "Tic Tac Toe"
               :resizable true
               :size [700 800]
               :setup setup-gui
               :update update-state
               :draw draw-state
               :mouse-clicked mouse-clicked
               :key-typed key-typed
               :features [:keep-on-top]
               :middleware [m/fun-mode])
  )



