SlashBlade
==========

本リポジトリの使い方
---------
1. 開発環境の作成
    * 用意できてるなら不要です。
    1. 初期設定
        * 必要なファイルのDL、デコンパイル等をします
        * 後述をコマンドプロンプトやbatファイル等で実行しましょう
            * Eclipseなら

            ~~~bat:setup.bat
            call gradlew.bat setupDevWorkspace setupDecompWorkspace eclipse
            pause
            ~~~

            * Intellij IDEAなら

            ~~~bat:setup.bat
            call gradlew.bat setupDevWorkspace setupDecompWorkspace idea
            pause
            ~~~

    2. S/Cの設定
        1. server設定
            * サーバを起動してみます。
            * eclipse/server.properties が生成されます
            * online-mode=false と書き換えましょう  これでデバッグ環境でローカルサーバに入れます。
        2. client設定
            * clientを起動します。
            * サーバ接続設定を追加します

            > Multiplayer > Add server > Server Address > localhost:25565

            * サーバ名は任意で

2. eclipse編
    1. デバッグまで
        1. リポジトリーCloneを作る
            * ビューを開いて、URLをCtrl+V辺りでペースト等、任意のディレクトリにClone作成します
            * ※ビューの開き方例 ウィンドウ>ビュー>その他 "Gitリポジトリー"
        2. プロジェクトにリンクフォルダとして追加

            > 新規＞フォルダー＞拡張＞リンクされたフォルダー

            * 1で作成した作業フォルダを指定します。

        3. ソースフォルダ指定
            * src/main/以下にある java と resourceを選択しソースフォルダとして登録
            * 右クリック＞ビルド･パス＞ソースフォルダーとして使用
        4. ライブラリの準備
            * setup.batを実行します
            * 自動で基本的に必要なライブラリのDLや初期設定がされます
            * ※本modでは特に追加ライブラリは無いので実行だけすれば終わりです。
        5. デバッグ実行
            c/s起動できるか試します。
    2. ビルド
        * build.batを実行します
        * build/libs 配下にjarが生成されます。

3. inteliJ IDEA編
    1. デバッグまで
        1. リポジトリーCloneを作る
            * 適宜GITClone作れるツールで任意のフォルダへCloneしてください
            * IDEA上で行う場合
                * ※あらかじめgitのコマンドラインツールをインストール＆登録しておいてください。

                > setting > "git"で検索 > git > git.exeの欄に自環境のパスを

                * ※パスが通っている場合は不要
                * 適宜githubアカウント設定など

                    > VCS>Checkout from Version Control>git

                * プロジェクトとして開くか聞かれるかもしれませんが No で
        2. 初期設定
            * setup.batを実行して下さい。

        3. Moduleのインポート
            * 1でCloneしたリポジトリーの作業ディレクトリから setup.bat実行で生成された *.imlを選択します。

            > File > Import Module

        4. 依存関係の設定
            * デバッグの起動設定等をもつ、メインのForgeモジュールで読み込むようにします。

            > ForgeのModuleSettings > Dependenciesタブ > + > ModuleDependency > 2でインポートしたModule

        5. デバッグ実行
            * Runから適宜 server / client 実行します。
    2. ビルド
        * Gradleタスクから build を実行します。
        * build/libs 配下にjarが生成されます。
