public class Othello {
	private int row = 8; // オセロ盤の縦横マス数(2の倍数のみ)
	private String turn; // 手番 → "black" か "white"（決着が付いたときのみ"end"）
	private String[][] board = new String[row][row]; // 盤面情報（row×row マス）
	private String handLevel; // ハンデレベル

	// コンストラクタ
	public Othello() {
		turn = "black"; // 黒が先手
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = "empty";
			}
			int center = row / 2;

			// 初期状態の配置
			board[center - 1][center - 1] = "black";
			board[center][center] = "black";
			board[center - 1][center] = "white";
			board[center][center - 1] = "white";
		}
	}

	// メソッド
	public void setHandLevel(String hand) { // ハンデレベルを受け取り、盤面に反映する
		handLevel = hand;
		if (handLevel.equals("2")) {
			board[0][0] = "black";
		}
		if (handLevel.equals("3")) {
			board[0][0] = "black";
			board[row - 1][row - 1] = "black";
		}
		if (handLevel.equals("4")) {
			board[0][0] = "black";
			board[row - 1][row - 1] = "black";
			board[0][row - 1] = "black";
		}
		if (handLevel.equals("5")) {
			board[0][0] = "black";
			board[row - 1][row - 1] = "black";
			board[0][row - 1] = "black";
			board[row - 1][0] = "black";
		}
	}

	public String getHandLevel() { // ハンデレベルの取得
		return handLevel;
	}


	public int getRow() { // 縦横のマス数を取得
		return row;
	}

	public String checkWinner() { // 勝敗を判断
		return "black";
	}

	public String getTurn() { // 手番情報を取得
		return turn;
	}

	public String[][] getBoard() { // 局面情報を取得
		return board;
	}

	// 手番を変更するメソッド changeTurn()
	public void changeTurn() {
		// 黒→白
		if (turn.equals("black")) {
			turn = "white";
		}
		// 白→黒
		else if (turn.equals("white")) {
			turn = "black";
		} else
			;
	}

	public boolean isGameover() { // 対局終了を判断
		return true;
	}

	// 盤面上にある指定された色のコマを数え上げるメソッドcount
	public int count(String color) {
		int n = 0;
		for (int i = 0; i < row * row; i++) {
			if (board[i / row][i % row].equals(color)) {
				n++;
			}
		}
		return n;
	}

	// 指定された色のコマを、盤面上のどこかに打てるかどうか判断するメソッドjudgePass(打つ場所がある場合はtrue、ない場合はfalseを返す)
	public boolean judgePass(String color) {
		int i = 0;
		for (; i < row * row; i++) {
			if (judge(i, color)) {
				break;
			}
		}
		// 置ける場合
		if (i < row * row) {
			return true;
		}
		// 置けない場合
		else {
			return false;
		}
	}

	// 指定したマスにその色のコマが置けるかどうか判定するメソッド judge（置ける場合はtrue、置けない場合はfalseを返す）
	public boolean judge(int p, String color) {
		int i = p / row; // 指定したマスのx座標
		int j = p % row; // 指定したマスのy座標

		// 指定されているマスが盤面からはみ出ている場合 → false を返す
		if ((0 <= i && i < row && 0 <= j && j < row) == false) {
			return false;
		}

		// 指定されたマスに既に黒か白が置かれている場合 → false を返す
		if ((board[i][j].equals("empty")) == false) {
			return false;
		}

		// 自分の色と相手の色を識別する
		String myColor, oppColor;
		if (color.equals("black")) {
			myColor = "black";
			oppColor = "white";
		} else {
			myColor = "white";
			oppColor = "black";
		}

		// 作業用変数
		int next_i, next_j;

		// 1. 下のマスを調査（置ける場合 → true を返してメソッドの処理は終了）
		// //////////////////////////////////////
		if (i + 1 < row) {// 下のマスが盤面をはみ出てはいけない
			if (board[i + 1][j].equals(oppColor)) {// 下のマスに相手のコマが置かれている場合
				next_i = i + 2; // 着目するマスを指定

				while (next_i < row) {// 着目するマスを下にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → true を返す
					if (board[next_i][j].equals(myColor)) {
						return true;
					}

					// 何も置かれていない場合 → ループを抜ける
					else if (board[next_i][j].equals("empty")) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ下へずれる
					next_i++;
				}
			}
		}

		// 2. 右のマスを調査（置ける場合 → true を返してメソッドの処理は終了）//////////////////////////////////////
		if (j + 1 < row) {// 右のマスが盤面をはみ出てはいけない
			if (board[i][j + 1].equals(oppColor)) {// 右のマスに相手のコマが置かれている場合
				next_j = j + 2; // 着目するマスを指定

				while (next_j < row) {// 着目するマスを右にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → true を返す
					if (board[i][next_j].equals(myColor)) {
						return true;
					}

					// 何も置かれていない場合 → ループを抜ける
					else if (board[i][next_j].equals("empty")) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ右へずれる
					next_j++;
				}
			}
		}

		// 3. 上のマスを調査（置ける場合 → true を返してメソッドの処理は終了）//////////////////////////////////////
		if (i - 1 >= 0) {// 上のマスが盤面をはみ出てはいけない
			if (board[i - 1][j].equals(oppColor)) {// 上のマスに相手のコマが置かれている場合
				next_i = i - 2; // 着目するマスを指定

				while (next_i >= 0) {// 着目するマスを上にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → true を返す
					if (board[next_i][j].equals(myColor)) {
						return true;
					}

					// 何も置かれていない場合 → ループを抜ける
					else if (board[next_i][j].equals("empty")) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ上へずれる
					next_i--;
				}
			}
		}

		// 4. 左のマスを調査（置ける場合 → true を返してメソッドの処理は終了）//////////////////////////////////////
		if (j - 1 >= 0) {// 左のマスが盤面をはみ出てはいけない
			if (board[i][j - 1].equals(oppColor)) {// 左のマスに相手のコマが置かれている場合
				next_j = j - 2; // 着目するマスを指定

				while (next_j >= 0) {// 着目するマスを左にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → true を返す
					if (board[i][next_j].equals(myColor)) {
						return true;
					}

					// 何も置かれていない場合 → ループを抜ける
					else if (board[i][next_j].equals("empty")) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ左へずれる
					next_j--;
				}
			}
		}

		// 5. 右下のマスを調査（置ける場合 → true
		// を返してメソッドの処理は終了）//////////////////////////////////////
		if (i + 1 < row && j + 1 < row) {// 右下のマスが盤面をはみ出てはいけない
			if (board[i + 1][j + 1].equals(oppColor)) {// 右下のマスに相手のコマが置かれている場合
				next_i = i + 2; // 着目するマスを指定
				next_j = j + 2;

				while (next_i < row && next_j < row) {// 着目するマスを右下にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → true を返す
					if (board[next_i][next_j].equals(myColor)) {
						return true;
					}

					// 何も置かれていない場合 → 盤面をいじらずにループを抜ける
					else if (board[next_i][next_j].equals("empty")) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ右下へずれる
					next_i++;
					next_j++;
				}
			}
		}

		// 6. 左下のマスを調査（置ける場合 → true
		// を返してメソッドの処理は終了）//////////////////////////////////////
		if (i + 1 < row && j - 1 >= 0) {// 左下のマスが盤面をはみ出てはいけない
			if (board[i + 1][j - 1].equals(oppColor)) {// 左下のマスに相手のコマが置かれている場合
				next_i = i + 2; // 着目するマスを指定
				next_j = j - 2;

				while (next_i < row && next_j >= 0) {// 着目するマスを左下にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → true を返す
					if (board[next_i][next_j].equals(myColor)) {
						return true;
					}

					// 何も置かれていない場合 → ループを抜ける
					else if (board[next_i][next_j].equals("empty")) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ左下へずれる
					next_i++;
					next_j--;
				}
			}
		}

		// 7. 右上のマスを調査（置ける場合 → true
		// を返してメソッドの処理は終了）//////////////////////////////////////
		if (i - 1 >= 0 && j + 1 < row) {// 右上のマスが盤面をはみ出てはいけない
			if (board[i - 1][j + 1].equals(oppColor)) {// 右上のマスに相手のコマが置かれている場合
				next_i = i - 2; // 着目するマスを指定
				next_j = j + 2;

				while (next_i >= 0 && next_j < row) {// 着目するマスを右上にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → true を返す
					if (board[next_i][next_j].equals(myColor)) {
						return true;
					}

					// 何も置かれていない場合 → ループを抜ける
					else if (board[next_i][next_j].equals("empty")) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ右上へずれる
					next_i--;
					next_j++;
				}
			}
		}

		// 8. 左上のマスを調査（置ける場合 → true
		// を返してメソッドの処理は終了）//////////////////////////////////////
		if (i - 1 >= 0 && j - 1 >= 0) {// 左上のマスが盤面をはみ出てはいけない
			if (board[i - 1][j - 1].equals(oppColor)) {// 左上のマスに相手のコマが置かれている場合
				next_i = i - 2; // 着目するマスを指定
				next_j = j - 2;

				while (next_i >= 0 && next_j >= 0) {// 着目するマスを左上にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → true を返す
					if (board[next_i][next_j].equals(myColor)) {
						return true;
					}

					// 何も置かれていない場合 → ループを抜ける
					else if (board[next_i][next_j].equals("empty")) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ左上へずれる
					next_i--;
					next_j--;
				}
			}
		}

		// どこににも置けない場合、false を返す
		return false;

	}

	// 指定したマスにその色のコマを置き、盤面情報を更新するメソッド（置けない場合は false を返す）
	public boolean putStone(int p, String color) {
		int i = p / row; // 指定したマスのx座標
		int j = p % row; // 指定したマスのy座標
		boolean put = false; // コマを置けたかどうか

		// 指定されているマスが盤面からはみ出ている場合 → false を返す
		if ((0 <= i && i < row && 0 <= j && j < row) == false) {
			return false;
		}
		// 指定されたマスに既に黒か白が置かれている場合 → false を返す
		if ((board[i][j].equals("empty")) == false) {
			return false;
		}

		// 自分の色と相手の色を識別する
		String myColor, oppColor;
		if (color.equals("black")) {
			myColor = "black";
			oppColor = "white";
		} else {
			myColor = "white";
			oppColor = "black";
		}

		// 作業用変数
		int next_i, next_j;

		// 1. 下のマスを調査（置ける場合は挟まれている相手のコマをひっくり返す）//////////////////////////////////////
		if (i + 1 < row) {// 下のマスが盤面をはみ出てはいけない
			if (board[i + 1][j].equals(oppColor)) {// 下のマスに相手のコマが置かれている場合
				next_i = i + 2; // 着目するマスを指定

				while (next_i < row) {// 着目するマスを上にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → 挟まれているコマを全て自分のコマにして、ループを抜ける
					if (board[next_i][j].equals(myColor)) {
						for (int a = 0; a < Math.abs(next_i - i); a++) {
							board[i + a][j] = myColor;
						}
						put = true;
						break;
					}

					// 何も置かれていない場合 → 盤面をいじらずにループを抜ける
					else if (board[next_i][j].equals("empty")) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ下へずれる
					next_i++;
				}
			}
		}

		// 2. 右のマスを調査（置ける場合は挟まれている相手のコマをひっくり返す）//////////////////////////////////////
		if (j + 1 < row) {// 右のマスが盤面をはみ出てはいけない
			if (board[i][j + 1].equals(oppColor)) {// 右のマスに相手のコマが置かれている場合
				next_j = j + 2; // 着目するマスを指定

				while (next_j < row) {// 着目するマスを右にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → 挟まれているコマを全て自分のコマにして、ループを抜ける
					if (board[i][next_j].equals(myColor)) {
						for (int a = 0; a < Math.abs(next_j - j); a++) {
							board[i][j + a] = myColor;
						}
						put = true;
						break;
					}

					// 何も置かれていない場合 → 盤面をいじらずにループを抜ける
					else if (board[i][next_j].equals("empty")) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ右へずれる
					next_j++;
				}
			}
		}

		// 3. 上のマスを調査（置ける場合は挟まれている相手のコマをひっくり返す）//////////////////////////////////////
		if (i - 1 >= 0) {// 上のマスが盤面をはみ出てはいけない
			if (board[i - 1][j].equals(oppColor)) {// 上のマスに相手のコマが置かれている場合
				next_i = i - 2; // 着目するマスを指定

				while (next_i >= 0) {// 着目するマスを上にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → 挟まれているコマを全て自分のコマにして、ループを抜ける
					if (board[next_i][j].equals(myColor)) {
						for (int a = 0; a < Math.abs(next_i - i); a++) {
							board[i - a][j] = myColor;
						}
						put = true;
						break;
					}

					// 何も置かれていない場合 → 盤面をいじらずにループを抜ける
					else if (board[next_i][j].equals("empty")) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ上へずれる
					next_i--;
				}
			}
		}

		// 4. 左のマスを調査（置ける場合は挟まれている相手のコマをひっくり返す）//////////////////////////////////////
		if (j - 1 >= 0) {// 左のマスが盤面をはみ出てはいけない
			if (board[i][j - 1].equals(oppColor)) {// 左のマスに相手のコマが置かれている場合
				next_j = j - 2; // 着目するマスを指定

				while (next_j >= 0) {// 着目するマスを左にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → 挟まれているコマを全て自分のコマにして、ループを抜ける
					if (board[i][next_j].equals(myColor)) {
						for (int a = 0; a < Math.abs(next_j - j); a++) {
							board[i][j - a] = myColor;
						}
						put = true;
						break;
					}

					// 何も置かれていない場合 → 盤面をいじらずにループを抜ける
					else if (board[i][next_j].equals("empty")) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ左へずれる
					next_j--;
				}
			}
		}

		// 5. 右下のマスを調査（置ける場合は挟まれている相手のコマをひっくり返す）//////////////////////////////////////
		if (i + 1 < row && j + 1 < row) {// 右下のマスが盤面をはみ出てはいけない
			if (board[i + 1][j + 1].equals(oppColor)) {// 右下のマスに相手のコマが置かれている場合
				next_i = i + 2; // 着目するマスを指定
				next_j = j + 2;

				while (next_i < row && next_j < row) {// 着目するマスを右下にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → 挟まれているコマを全て自分のコマにして、ループを抜ける
					if (board[next_i][next_j].equals(myColor)) {
						for (int a = 0; a < Math.abs(next_i - i); a++) {
							board[i + a][j + a] = myColor;
						}
						put = true;
						break;
					}

					// 何も置かれていない場合 → 盤面をいじらずにループを抜ける
					else if (board[next_i][next_j].equals(myColor)) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ右下へずれる
					next_i++;
					next_j++;
				}
			}
		}

		// 6. 左下のマスを調査（置ける場合は挟まれている相手のコマをひっくり返す）//////////////////////////////////////
		if (i + 1 < row && j - 1 >= 0) {// 左下のマスが盤面をはみ出てはいけない
			if (board[i + 1][j - 1].equals(oppColor)) {// 左下のマスに相手のコマが置かれている場合
				next_i = i + 2; // 着目するマスを指定
				next_j = j - 2;

				while (next_i < row && next_j >= 0) {// 着目するマスを左下にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → 挟まれているコマを全て自分のコマにして、ループを抜ける
					if (board[next_i][next_j].equals(myColor)) {
						for (int a = 0; a < Math.abs(next_i - i); a++) {
							board[i + a][j - a] = myColor;
						}
						put = true;
						break;
					}

					// 何も置かれていない場合 → 盤面をいじらずにループを抜ける
					else if (board[next_i][next_j].equals(myColor)) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ左下へずれる
					next_i++;
					next_j--;
				}
			}
		}

		// 7. 右上のマスを調査（置ける場合は挟まれている相手のコマをひっくり返す）//////////////////////////////////////
		if (i - 1 >= 0 && j + 1 < row) {// 右上のマスが盤面をはみ出てはいけない
			if (board[i - 1][j + 1].equals(oppColor)) {// 右上のマスに相手のコマが置かれている場合
				next_i = i - 2; // 着目するマスを指定
				next_j = j + 2;

				while (next_i >= 0 && next_j < row) {// 着目するマスを右上にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → 挟まれているコマを全て自分のコマにして、ループを抜ける
					if (board[next_i][next_j].equals(myColor)) {
						for (int a = 0; a < Math.abs(next_i - i); a++) {
							board[i - a][j + a] = myColor;
						}
						put = true;
						break;
					}

					// 何も置かれていない場合 → 盤面をいじらずにループを抜ける
					else if (board[next_i][next_j].equals(myColor)) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ右上へずれる
					next_i--;
					next_j++;
				}
			}
		}

		// 8. 左上のマスを調査（置ける場合は挟まれている相手のコマをひっくり返す）//////////////////////////////////////
		if (i - 1 >= 0 && j - 1 >= 0) {// 左上のマスが盤面をはみ出てはいけない
			if (board[i - 1][j - 1].equals(oppColor)) {// 左上のマスに相手のコマが置かれている場合
				next_i = i - 2; // 着目するマスを指定
				next_j = j - 2;

				while (next_i >= 0 && next_j >= 0) {// 着目するマスを左上にずらしながら無限ループ（盤面の範囲を超えるまで）
					// 自分のコマが置かれている場合 → 挟まれているコマを全て自分のコマにして、ループを抜ける
					if (board[next_i][next_j].equals(myColor)) {
						for (int a = 0; a < Math.abs(next_i - i); a++) {
							board[i - a][j - a] = myColor;
						}
						put = true;
						break;
					}

					// 何も置かれていない場合 → 盤面をいじらずにループを抜ける
					else if (board[next_i][next_j].equals(myColor)) {
						break;
					}

					// 相手のコマが置かれている場合 → ループ続行
					else
						;

					// もう一つ左上へずれる
					next_i--;
					next_j--;
				}
			}
		}

		// コマを置けた場合の処理
		if (put == true) {
			
			//次の手が自分で自分の置く場所がないとき
			if( (myColor.equals(turn) && !judgePass(myColor)) ) {
				
			}
			
			
			// 次の手で相手の置く場所がある場合 → 手番を変える
			if (judgePass(oppColor)) {
				turn = oppColor;
			}
			// 次の手で相手の置く場所がない場合
			else {
				// 次の手で自分と相手どちらも置く場所がない場合 → 手番を"end" に変え、決着が付いたことを示す
				if ((judgePass(myColor)) == false) {
					turn = "end";
				}
				// 次の手で相手の置く場所はないが、自分の置く場所がある場合 → 何もしない（手番が自分のまま）
				else {
				}
			}

		}

		return put;
	}
}