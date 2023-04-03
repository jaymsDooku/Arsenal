package dev.jayms.arsenal.util;

import org.bukkit.block.BlockFace;

public class BlockFaceTransform {
    
    private BlockFace from;
    private BlockFace to;

    public BlockFaceTransform(BlockFace from, BlockFace to) {
        this.from = from;
        this.to = to;
    }

    public BlockFace getFrom() {
        return from;
    }

    public BlockFace getTo() {
        return to;
    }

    public int getRotateAngle() {
        if (from == to) {
            return 0;
        }
        if (from == BlockFace.NORTH) {
            if (to == BlockFace.EAST) {
                return 270;
            } else if (to == BlockFace.SOUTH) {
                return 180;
            } else if (to == BlockFace.WEST) {
                return 90;
            }
        } else if (from == BlockFace.SOUTH) {
            if (to == BlockFace.WEST) {
                return 270;
            } else if (to == BlockFace.NORTH) {
                return 180;
            } else if (to == BlockFace.EAST) {
                return 90;
            }
        } else if (from == BlockFace.EAST) {
            if (to == BlockFace.SOUTH) {
                return 270;
            } else if (to == BlockFace.WEST) {
                return 180;
            } else if (to == BlockFace.NORTH) {
                return 90;
            }
        } else if (from == BlockFace.WEST) {
            if (to == BlockFace.NORTH) {
                return 270;
            } else if (to == BlockFace.EAST) {
                return 180;
            } else if (to == BlockFace.SOUTH) {
                return 90;
            }
        }

        return 0;
    }

    public BlockFace rotateBlockFace(BlockFace blockFace) {
        int angle = getRotateAngle();
        if (blockFace == BlockFace.NORTH) {
            switch (angle) {
                case 270:
                    return BlockFace.EAST;
                case 180:
                    return BlockFace.SOUTH;
                case 90:
                    return BlockFace.WEST;
                default:
                    break;
            }
        } else if (blockFace == BlockFace.SOUTH) {
            switch (angle) {
                case 270:
                    return BlockFace.WEST;
                case 180:
                    return BlockFace.NORTH;
                case 90:
                    return BlockFace.EAST;
                default:
                    break;
            }
        } else if (blockFace == BlockFace.EAST) {
            switch (angle) {
                case 270:
                    return BlockFace.SOUTH;
                case 180:
                    return BlockFace.WEST;
                case 90:
                    return BlockFace.NORTH;
                default:
                    break;
            }
        } else if (blockFace == BlockFace.WEST) {
            switch (angle) {
                case 270:
                    return BlockFace.NORTH;
                case 180:
                    return BlockFace.EAST;
                case 90:
                    return BlockFace.SOUTH;
                default:
                    break;
            }
        }

        return blockFace;
    }

    private AffineTransform affineTransform;

    public AffineTransform getAffineTransform() {
        if (affineTransform != null) {
            return affineTransform;
        }

        if (from == to) {
            affineTransform = new AffineTransform();
        }
        if (from == BlockFace.NORTH) {
            if (to == BlockFace.EAST) {
                this.affineTransform = new AffineTransform().rotateY(270);
            } else if (to == BlockFace.SOUTH) {
                this.affineTransform = new AffineTransform().rotateY(180);
            } else if (to == BlockFace.WEST) {
                this.affineTransform = new AffineTransform().rotateY(90);
            }
        } else if (from == BlockFace.SOUTH) {
            if (to == BlockFace.WEST) {
                this.affineTransform = new AffineTransform().rotateY(270);
            } else if (to == BlockFace.NORTH) {
                this.affineTransform = new AffineTransform().rotateY(180);
            } else if (to == BlockFace.EAST) {
                this.affineTransform = new AffineTransform().rotateY(90);
            }
        } else if (from == BlockFace.EAST) {
            if (to == BlockFace.SOUTH) {
                this.affineTransform = new AffineTransform().rotateY(270);
            } else if (to == BlockFace.WEST) {
                this.affineTransform = new AffineTransform().rotateY(180);
            } else if (to == BlockFace.NORTH) {
                this.affineTransform = new AffineTransform().rotateY(90);
            }
        } else if (from == BlockFace.WEST) {
            if (to == BlockFace.NORTH) {
                this.affineTransform = new AffineTransform().rotateY(270);
            } else if (to == BlockFace.EAST) {
                this.affineTransform = new AffineTransform().rotateY(180);
            } else if (to == BlockFace.SOUTH) {
                this.affineTransform = new AffineTransform().rotateY(90);
            }
        }

        return affineTransform;
    }
    
}
