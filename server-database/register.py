import pymongo

"""
TODO: 
INPUTS: 
        username, password: strings
        tb: mongo db table contains all users
        valid: whether or not error occured
OUTPUTS:
        success: 1 if user is created successfully
                 0 if username already existed
"""
def run_register(username,password,tb,valid):    
        query = {'user_id' : username, 'valid' : 1}
        try:
                result = list(tb.find(query))
                if result:
                        return 0

                else:
                        row_id = tb.find().count()
                        entry = {'row_id': row_id, 
                                'user_id': username,
                                'password': password,
                                'valid': 1}
                        try:
                                tb.insert_one(entry)
                                return 1
                        except Exception as e:
                                valid = False
                                print(str(e))
                                return None
        except Exception as e:
                valid = False
                print(str(e))
                return None
