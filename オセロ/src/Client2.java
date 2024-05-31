
//パッケージのインポート
import javax.swing.JOptionPane;

public class Client2 {
	// mainメソッド
	public static void main(String args[]) {
		// 名前の入力
		String myName = JOptionPane.showInputDialog(null, "名前を入力してください", "名前の入力", JOptionPane.QUESTION_MESSAGE);
		if (myName.equals("")) {
			myName = "No name";// 名前がないときは，"No name"とする
		}
		// 希望ハンデレベルの選択
		String selectvalues[] = { "0", "1", "2", "3", "4", "5" };
		Object hopeHand = JOptionPane.showInputDialog(null, "希望のハンデレベルを入力してください", "ハンデレベルの入力",
				JOptionPane.INFORMATION_MESSAGE, null, selectvalues, selectvalues[0]);

		// プレイヤオブジェクトの用意
		Player player = new Player();
		player.setName(myName); // 名前をセット
		player.setHopeHand(Integer.parseInt(hopeHand.toString()));// 希望ハンデレベルをセット

		// オセロオブジェクトの用意
		Othello game = new Othello();

		// クライアントオブジェクトの用意
		Client oclient = new Client(game, player); // 引数としてオセロオブジェクト、プレイヤオブジェクトを渡す
		oclient.setVisible(true);
		oclient.connectServer(args[0], 10000, player.getHopeHand());// サーバ接続（サーバのIPアドレス、ポート番号、希望ハンデレベルを与える）
	}
}