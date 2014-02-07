SlashBlade
==========
srcの使い方
---------------------------------
1.本プロジェクトをgitリポジトリCloneをあらかじめ用意するとかしとく
2. forgeGradle環境用意する。 eclipseのWorkSpaceの用意
forgeフォルダ直下に適当なbatファイル作って
gradlew.bat setupDevWorkspace eclipse
とか書いて実行すれば環境完成。
いつもどおり、eclipseフォルダを eclipseのworkSpaceとして読み込む。
3. EclipseからgitポジトリCloneしてできたフォルダを、Minecraftプロジェクト配下にリンクフォルダとして追加
  新規＞フォルダー＞拡張＞リンクされたフォルダー
4. リンクされたフォルダ中のsrc/main/以下にある java と resourceを選択しソースフォルダとして登録
  右クリック＞ビルド･パス＞ソースフォルダーとして使用
5. Eclipseでデバッグする
  ASM部分のデバッグは、面倒なので各々しらべて調べるがよろしい
  実環境テストで済ませてます
6.ビルド準備
setup.batがポジトリの中にあるので実行する。
ビルド用の初期設定、次回以降不要（1の作業と一緒だけどビルド用だから簡易になってる）
7.ビルド
build.batがポジトリの中にあるので実行
build/libs にjarが生成されるはず
8.バージョン表記とかの変更
build.gradleみて。s