抜刀剣
=================

機能一覧
---------

1. コンボルート
    * 地上
        * 左：切り上げ
        単体 敵拘束 打ち上げ attackableList無視
            * 左：切り下ろし
            単体 落下ダメージ確定 叩き落し attackableList無視
            * 右：切り下ろし
            範囲 落下ダメージ確定 叩き落し
        * 右：鞘打ち出し
        敵拘束  殺傷なし
            * 左：切り上げ
            単体 敵拘束 打ち上げ attackableList無視
            * 右：鞘戻し
            敵拘束  殺傷なし
                * 右：抜刀
                KnockbackII以上効果
    * 空中
	    * 右：空中居合い
	    単体 敵拘束  attackableList無視
	        * 右：抜刀
	        単体 KnockbackII以上効果  attackableList無視
	        * 左：抜刀
	        範囲 KnockbackII以上効果
	    * 左：空中居合い
	    範囲 敵拘束
	        * 右：抜刀
	        単体 KnockbackII以上効果  attackableList無視
	        * 左：抜刀
	        範囲 KnockbackII以上効果

    * 常時派生
        * 右溜め：SlashDimension
        範囲 敵拘束 ProudSoul:20 or 耐久10 ロック対象はattackableList無視

刀一覧
--------

1. 無銘刀「木偶」
    * 木 60 原木
2. 無銘刀「竹光」
    * 石 50 竹
3. 名刀「銀紙竹光」
    * 鉄 40 竹
4. 利刀「白鞘」
    * 鉄 70 鉄インゴット 耐久０時消滅なし
5. 大太刀「」
    * ダイヤ 50 鉄インゴット 耐久０時消滅なし
    * エンチャントでSD開放
    * 加えて名付けでＥｘｔｒａ能力開放

その他能力
-----------

* ロック
    * スニーク中、近傍のAttackable敵をロック
    * カメラ中心に居る相手を優先※その場合は、AttackableList無視
    * スニークを解かない限り、射程外でもロック継続
* 矢切り
    * どのモーションでも可能
    * 範囲は各モーションでの攻撃範囲と同等
    * モーション中判定あり
    * ガスト火球は打ち返し
* ProudSoul  (PS)
    * 所持状態で経験値を吸収すると増える
    * Ex 妖刀時に1000を超えると、経験値吸収時に即時耐久回復
* KillCount
    * トドメを刀で刺すと加算
* 攻撃力Lv補正
    * KillCount 1000を超えると全ての剣で、Lvを攻撃力に特定の割合で加算
* 魔法ダメージ
    * Power付与でSD、近接魔法ダメージの追加攻撃
*  簡易修復
    * 丸石１とPS２０で耐久１回復
    * 着火具みたいなレシピ
    * 丸石は纏め置き可能
* 金床修復
    * SPに(エンチャント数+1)*100　のボーナス付与
    * 共通修理素材あり
* 折れる
    * 金属製の刃の場合、耐久が0となった時点では消滅せずに残る
    * 攻撃力が激減する他、攻撃範囲も狭くなる
* 刀の魂片
    * 刀の折れた際にドロップ
    * ProudSoulの保有量で増減
* 共通修理素材
    * 刀の魂片 必要Lv-10効果
    * 刀の魂塊 必要Lv-25効果
    * 刀の魂珠 必要Lv-50効果
* Ex 打ち返し
    * 妖刀時は、ブレイズ火球や矢も打ち帰し可能
* Ex 反射
    * 棘鎧をエンチャントすると、打ち返しが、射手へ自動で向くように
* Ex SlashDimension(SD)
    * 大太刀「」にエンチャント付与で開放
    * 視線方向の近傍敵を中心に範囲攻撃
    * ロック中は、必ずロック対象を中心に発動
    * ロック対象は、射程 及び AttackableList無視で攻撃
    * ※近接ダメージ扱い
* Ex 納刀
    * 抜刀系攻撃から派生
        * ※SDや鞘打ち以外
    * Attackableが近くに居る状況で、最後の攻撃から移動して居ない場合追加効果
    * 攻撃＆防御の高レベルポーション効果が極短時間発動
* Ex 自己修復
    * 妖刀時メインスロットに有り、かつ使用中で無い場合、経験値と満腹度を消費してゆっくりと修復する
    * 折れている場合は、高速修復
* Ex 即時修復
    * 妖刀時にProudSoulが1000を超えると、経験値オーブを取得する際、その分即時修復される
* Ex FeatherFall
    * 妖刀時エンチャント付与で特殊効果
    * 空中切り2回まで落下ダメージ無効化発動
    * 更に落下した場合は、その箇所からの落下ダメージ