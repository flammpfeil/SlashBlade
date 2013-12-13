SlashBlade
==========

・srcの使い方
0.本プロジェクトをgitリポジトリCloneをあらかじめ用意するとかしとく
　親フォルダは、ビルド用Forgeや、packageされたjarが置かれる作業フォルダとなるため階層に注意

※フォルダ構成例
root
+build ※ビルド用Forgeがbuild.xmlにより生成される。
　必要なライブラリなどはここに自動で揃うため、適宜開発環境のlibへコピー,及びビルドパス追加をする
+dist ※ビルドされpackage済みのjarがここに生成される。
+download ※ビルド環境作成用のtempフォルダ
+SlashBlade 本modポジトリフォルダ gitで管理する、このフォルダをリンクする

1.ForgeDev＃871環境を用意する。（#953までであれば使えるかも　ASM部分は注意
2.EclipseからSlashBladeフォルダを、Minecraftプロジェクト配下にリンクフォルダとして追加。
　新規＞フォルダー＞拡張＞リンクされたフォルダー
3.リンクされたフォルダ中のcommon resourceを選択しソースフォルダとして登録
　右クリック＞ビルド･パス＞ソースフォルダーとして使用
4.build.xmlをant実行する
　※ターゲットは、デフォルトのtestを利用するとBuild用ForgeのDLからjar作成まで全て行う
　※2回目以降のコンパイルは、install-dependenciesを除いた
　　recompile reobfcate package　を順に行えば短縮できる。

5.Eclipseでデバッグする
※ASM部分は考えずにデバッグする方法　Forgeイベントのエミュレーションを追加しているだけなので無視していいはず
InitProxyClientのコメントアウト行を有効にする
デバッグ起動できるはず。