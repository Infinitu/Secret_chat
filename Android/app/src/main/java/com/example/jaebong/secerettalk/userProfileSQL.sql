-- UserProfile & MyData
CREATE TABLE IF NOT EXISTS UserProfiles(id text primary key not null, nickName text not null, age int not null, gender text not null, bloodType text not null, imageUrl text not null, chatLevel int not null, gentle int not null, cool int not null, pervert int not null, common int not null)
CREATE TABLE IF NOT EXISTS MyData(accessToken text not null, nickName text not null, age int not null, gender text not null, bloodType text not null, imageUrl text not null, chatLevel int not null, gentle int not null, cool int not null, pervert int not null, common int not null, nickNameTag text not null)


-- Messages
CREATE TABLE IF NOT EXISTS Messages(type text not null, imageUrl text not null, address text not null, sender text not null, message text, sendTime long not null )
INSERT INTO Messages(type,imageUrl,address,sender,message,sendTime) VALUES()