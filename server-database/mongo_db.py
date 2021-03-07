import pymongo

# build a connection to a specific
# host_name: string
# port: int
# return a client
def create_client(host_name='localhost',port=27017):
    try:
        client = pymongo.MongoClient(host_name,port)
        print("Successfully start client")
        return client
    except Exception as e:
        print(str(e))
        exit(1)

# get a database, if not exist, create one
# db_name: string
# return the existing database
def get_database(client,db_name):
    try:
        db = client[db_name]
        print("Successfully created database")
        return db
    except Exception as e:
        print(str(e))
        exit(1)

# get a collection (table) in a database
# create one if not existed
# tb_name:string
# db: database
# return a collection
def get_collection(db,tb_name):
    try:
        tb = db[tb_name]
        print("Successfully created collection")
        return tb
    except Exception as e:
        print(str(e))
        exit(1)
        
# return all created dbs
def print_all_db(client):
    return client.list_database_names()
# return all created collections
def print_all_collection(db):
    return db.list_collection_names()
    
