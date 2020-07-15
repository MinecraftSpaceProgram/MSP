package io.github.MinecraftSpaceProgram.MSP.tileentity;

import io.github.MinecraftSpaceProgram.MSP.block.FlightComputer;
import io.github.MinecraftSpaceProgram.MSP.container.FlightComputerContainer;
import io.github.MinecraftSpaceProgram.MSP.init.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class FlightComputerTileEntity extends LockableLootTileEntity {

    private NonNullList<ItemStack> chestContents = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
    protected int numPlayerUsing;
    private IItemHandlerModifiable items = createHandler();
    private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> items);

    public FlightComputerTileEntity(TileEntityType<?> typeIn) {
        super(typeIn);
    }

    public FlightComputerTileEntity(){
        this(ModTileEntityTypes.FLIGHT_COMPUTER.get());
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.chestContents = itemsIn;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.flight_computer");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new FlightComputerContainer(id, player, this);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if(!this.checkLootAndRead(compound)) {
            ItemStackHelper.saveAllItems(compound, this.chestContents);
        }
        return compound;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if(!this.checkLootAndRead(compound)){
            ItemStackHelper.loadAllItems(compound, this.chestContents);
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if(id == 1){
            this.numPlayerUsing = type;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if(!player.isSpectator()){
            if(this.numPlayerUsing < 0){
                this.numPlayerUsing = 0;
            }

            ++this.numPlayerUsing;
            this.onOpenorClose();
        }
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        if(!player.isSpectator()){
            --this.numPlayerUsing;
            this.onOpenorClose();
        }
    }

    protected void onOpenorClose(){
        Block block = this.getBlockState().getBlock();
        if(block instanceof FlightComputer) {
            this.world.addBlockEvent(this.pos, block, 1, this.numPlayerUsing);
            this.world.notifyNeighborsOfStateChange(pos, block);
        }
    }

    public static int getPlayersUsing(IBlockReader reader, BlockPos pos){
        BlockState blockState = reader.getBlockState(pos);
        if(blockState.hasTileEntity()){
            TileEntity tile = reader.getTileEntity(pos);
            if(tile instanceof FlightComputerTileEntity){
                return ((FlightComputerTileEntity)tile).numPlayerUsing;
            }
        }
        return 0;
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        if(this.itemHandler != null){
            this.itemHandler.invalidate();
            this.itemHandler = null;
        }
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private IItemHandlerModifiable createHandler(){
        return new InvWrapper(this);
    }

    @Override
    public void remove() {
        super.remove();
        if(this.itemHandler != null){
            this.itemHandler.invalidate();
            this.itemHandler = null;
        }
    }
}
