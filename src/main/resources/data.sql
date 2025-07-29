-- seed assets
INSERT INTO asset(asset_symbol, asset_name)
VALUES
('ETH', 'Ethereum'),
('BTC', 'Bitcoin'),
('USDT', 'Tether')
;

-- seed with intial users
INSERT INTO users(user_name)
VALUES
('Bob'),
('Harriet')
;

-- seed initial price currency is USDT
INSERT INTO aggregated_price(asset, bid_price, ask_price, currency, bid_price_source, ask_price_source, last_updated_time)
VALUES
('ETH',3877.32, 3876.16, 'USDT', 'BINANCE', 'HUOBI', current_timestamp ),
('BTC', 118882.36, 118874.01, 'USDT', 'BINANCE', 'HUOBI', current_timestamp)
;

-- seed initial balance
INSERT INTO wallet_balance(user_id, asset, balance)
VALUES
(1,'USDT', 50000 ),
(2,'USDT', 50000 )
;
