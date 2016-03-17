import static Common.*

def val = 'val'

saveValue 'val', val
def storedVal = loadValue('val')

assert storedVal == val
 